package pcd.ass3.sudoku;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import com.rabbitmq.client.Delivery;

public class StreamRabbitDistributor implements DataDistributor {

  private record QueueConfigs(
    boolean durable, 
    boolean notExclusive, 
    boolean notAutoDelete, 
    Map<String,Object> params){};

  private final String BOARD_REGISTRY = "board-registry";
  private final String BOARD_UPDATE = "edits";
  private final String USER_UPDATE = "user-cursors";
  private static final int PREFETCH_COUNT = 200;

  private SharedDataListener updateListener;
  private Optional<String> boardName;
  private Optional<Channel> channel;
  private Map<String, Optional<String>> consumerTag;
  private final QueueConfigs configs = new QueueConfigs(true, false, false, Collections.singletonMap("x-queue-type", "stream"));

    @Override
    public void init(SharedDataListener controller) {
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
    public void shareUpdate(JsonData edits) {
      publishTo(edits.getJsoString(), BOARD_UPDATE);
    }

    @Override
    public void updateCursor(JsonData userInfo) {
      publishTo(userInfo.getJsoString(), USER_UPDATE);
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

    private void publishTo(String jsonMsg, String queueName) {
      boardName.ifPresent(name -> {
        this.channel.ifPresent(c -> {
          try {
            c.basicPublish("", concat(name, queueName), null, jsonMsg.getBytes("UTF-8"));
          } catch (IOException exc) {
            this.updateListener.notifyErrors("Publish error", exc);
          }
        });
      });
    }

    private String concat(String name, String queueName) {
      return String.join("-", name, queueName);
    }

    @Override
    public void joinBoard(String nickname, String boardName) {
      this.boardName = Optional.of(boardName);
      String edits = concat(boardName, BOARD_UPDATE);
      String usersc = concat(boardName, USER_UPDATE);
      // Optional<String> jsonBoard = Optional.empty();
      // try {
      //   if (!queueExists(edits)) {
      //     BoardState board = new Messages.BoardState(initBoard);
      //     jsonBoard = toJson(board);
      //   }
      // } catch (TimeoutException exc) {
      //   this.updateListener.notifyErrors("Join board error", exc);
      // }

      declareQueue(edits);
      declareQueue(usersc);
      consumeMessages(edits, msg -> {
        try {
          String msgBody = new String(msg.getBody(), "UTF-8");
          //maybe here i wanna get some info from msg
          updateListener.boardUpdate((JsonData) () -> msgBody);
        } catch (UnsupportedEncodingException ex) {}
      });
      consumeMessages(usersc, msg -> {
        try {
          String msgBody = new String(msg.getBody(), "UTF-8");
          updateListener.cursorsUpdate((JsonData) () -> msgBody);
        } catch (UnsupportedEncodingException exc) {
          this.updateListener.notifyErrors("Parsing error cursor data", exc);
        }
      });
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

    private void consumeMessages(String queueName, Consumer<Delivery> consume) {
      Optional<String> tag = Optional.empty();
      if (channel.isPresent()) {
        Channel ch = channel.get();
        boolean autoAck = false;
        try {
          ch.basicQos(PREFETCH_COUNT);
          String cTag = ch.basicConsume(
            queueName,
            autoAck,
            Collections.singletonMap("x-stream-offset", "first"),
            (consTag, msg) -> {
              consume.accept(msg);
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

    @Override
    public JsonData existingBoards() {
      //TODO: implement that
      throw new UnsupportedOperationException("Not supported yet.");
    }
  
}
