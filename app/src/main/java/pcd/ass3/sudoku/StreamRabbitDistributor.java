package pcd.ass3.sudoku;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

import pcd.ass3.sudoku.domain.Messages;
import pcd.ass3.sudoku.domain.Messages.BoardState;
import pcd.ass3.sudoku.domain.Messages.CellUpdate;
import pcd.ass3.sudoku.utils.Pair;

public class StreamRabbitDistributor implements DataDistributor {

  private record QueueConfigs(
    boolean durable, 
    boolean notExclusive, 
    boolean notAutoDelete, 
    Map<String,Object> params){};

  private enum UpdateType {
    BOARD_UPDATE("edits"),
    USER_UPDATE("user-cursors");

    private final String name;

    UpdateType(String name) {
      this.name = name;
    }
    public String getDstName() {
      return name;
    }
  }

  private UpdateListener updateListener;
  private Optional<String> boardName;
  private Optional<Channel> channel;
  private Map<String, Optional<String>> consumerTag;
  private final QueueConfigs configs = new QueueConfigs(true, false, false, Collections.singletonMap("x-queue-type", "stream"));

    @Override
    public void init(UpdateListener controller) {
      this.updateListener = controller;
      this.channel = createChannel();
      this.consumerTag = new HashMap<>();
    }

    private Optional<Channel> createChannel()  {
      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("localhost");
      Optional<Channel> optChannel = Optional.empty();
      try {
        optChannel = factory.newConnection().openChannel();
      } catch (IOException | TimeoutException exc) {
        updateListener.notifyErrors("Init error channel creation fail", exc);
      }
      return optChannel;
    }

    @Override
    public void shareUpdate(Messages.UserEdit edits) {
      toJson(edits).ifPresent(json -> publishTo(json, UpdateType.BOARD_UPDATE));
    }

    @Override
    public void updateCursor(Messages.UserInfo userInfo) {
      toJson(userInfo).ifPresent(json -> publishTo(json, UpdateType.USER_UPDATE));
    }

    private Optional<String> toJson(Object obj) {
      Optional<String> data = Optional.empty();
      ObjectMapper mapper = new ObjectMapper();
      try {
        data = Optional.ofNullable(mapper.writeValueAsString(obj));
      } catch (JsonProcessingException exc) {
        this.updateListener.notifyErrors("Json parsing error", exc);
      }
      return data;
    }

    private void publishTo(String jsonMsg, UpdateType type) {
      boardName.ifPresent(name -> {
        this.channel.ifPresent(c -> {
          try {
            c.basicPublish("", queueNameFor(name, type), null, jsonMsg.getBytes("UTF-8"));
          } catch (IOException exc) {
            this.updateListener.notifyErrors("Publish error", exc);
          }
        });
      });
    }

    private String queueNameFor(String name, UpdateType type) {
      return name.concat("-").concat(type.getDstName());
    }

    @Override
    public void joinBoard(String nickname, String boardName, Map<Pair, Integer> initBoard) {
      this.boardName = Optional.of(boardName);
      String edits = queueNameFor(boardName, UpdateType.BOARD_UPDATE);
      String usersc = queueNameFor(boardName, UpdateType.USER_UPDATE);
      Optional<String> jsonBoard = Optional.empty();
      try {
        if (!queueExists(edits)) {
          BoardState board = new Messages.BoardState(initBoard);
          jsonBoard = toJson(board);
        }
      } catch (TimeoutException exc) {
        this.updateListener.notifyErrors("Join board error", exc);
      }

      declareQueue(edits);
      declareQueue(usersc);

      ObjectMapper mapper = new ObjectMapper();

      consumeMessages(edits, msg -> {   
        try {
          Messages.UserEdit recvEdits = mapper.readValue(msg, Messages.UserEdit.class);
          if (null == recvEdits.type()) {
              updateListener.notifyErrors("Message unknown", null);
          } else switch (recvEdits.type()) {
                case BOARD_CREATION -> {
                    BoardState board = mapper.readValue(recvEdits.jsonData(), Messages.BoardState.class);
                    updateListener.joined(board);
              }
                case CELL_UPDATE -> {
                    CellUpdate cell = mapper.readValue(recvEdits.jsonData(), Messages.CellUpdate.class);
                    updateListener.boardUpdate(cell);
              }
                default -> updateListener.notifyErrors("Message unknown", null);
            }
        } catch (Exception exc) {
          this.updateListener.notifyErrors("Parsing error board updates", exc);
        }
      });
      consumeMessages(usersc, msg -> {
        try {
            Messages.UserInfo usrInfo = mapper.readValue(msg, Messages.UserInfo.class);
            updateListener.cursorsUpdate(usrInfo);
        } catch (Exception exc) {
          this.updateListener.notifyErrors("Parsing error cursor data", exc);
        }
      });
      jsonBoard.ifPresent(bj -> shareUpdate(new Messages.UserEdit(nickname, Messages.DataType.BOARD_CREATION, bj)));
    }

    private Boolean queueExists(String queueName) throws TimeoutException {
      try {
        var checkChannel = createChannel();
        if (checkChannel.isPresent()) {
          checkChannel.get().queueDeclarePassive(queueName);
          return true;
        }
      } catch (IOException ex) {
        return false;
      }
      return false;
    }

    private void declareQueue(String name) {
      channel.ifPresent((ch) -> {
        try {
          ch.queueDeclare(name,
                  configs.durable,
                  configs.notExclusive,
                  configs.notAutoDelete,
                  configs.params);
        } catch (IOException exc) {
          this.updateListener.notifyErrors("Declaration queue", exc);
        }
      });
    }

    private void consumeMessages(String queueName, Consumer<String> consume) {
      Optional<String> tag = Optional.empty();
      if (channel.isPresent()) {
        Channel ch = channel.get();
        boolean autoAck = false;
        try {
          ch.basicQos(200);
          String cTag = ch.basicConsume(
            queueName,
            autoAck,
            Collections.singletonMap("x-stream-offset", "first"),
            (consTag, msg) -> {
              String msgBody = new String(msg.getBody(), "UTF-8");
              consume.accept(msgBody);
              long deliveryTag = msg.getEnvelope().getDeliveryTag();
              ch.basicAck(deliveryTag, true);
            },
            consTag -> {
                System.out.println(consTag + " disconnected from " +  queueName);
            }
          );
          tag = Optional.of(cTag);
        } catch (IOException exc) {
          this.updateListener.notifyErrors("Consuming Message", exc);
        }
      }
      this.consumerTag.put(queueName, tag);
    }

    @Override
    public void leaveBoard() {
      this.channel.ifPresent(ch -> {
        this.consumerTag.entrySet().forEach(e -> {
          String queueName = e.getKey();
          Optional<String> consTag = e.getValue();
          consTag.ifPresent(t -> {
            try {
              ch.basicCancel(t);
              this.consumerTag.put(queueName, Optional.empty());
            } catch (IOException ex) {
              this.updateListener.notifyErrors("Channel not exist", ex);
            }
          });
        });
        boolean allDisconnected = this.consumerTag.values().stream().allMatch(Optional::isEmpty);
        if (allDisconnected) {
          this.boardName = Optional.empty();
        }
        this.updateListener.boardLeft(allDisconnected);
      });
    }
  
}
