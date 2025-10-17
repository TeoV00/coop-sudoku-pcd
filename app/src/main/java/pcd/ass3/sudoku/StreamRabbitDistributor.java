package pcd.ass3.sudoku;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Delivery;

public class StreamRabbitDistributor implements DataDistributor {

  private record QueueConfigs(
    boolean durable, 
    boolean notExclusive, 
    boolean notAutoDelete, 
    Map<String,Object> params){};

  private final String REGISTRY_QUEUE_NAME = "board-registry";
  private final List<JsonData> boardRegistry = new ArrayList<>();
  private final String BOARD_UPDATE = "edits";
  private final String USER_UPDATE = "user-cursors";
  private static final int PREFETCH_COUNT = 200;

  private SharedDataListener updateListener;
  private Optional<String> boardName;
  private Optional<Channel> channel;
  private Map<String, Optional<String>> consumerTag;
  private final QueueConfigs DEFAULT_QUEUE_CONFIG = new QueueConfigs(true, false, false, Collections.singletonMap("x-queue-type", "stream"));

    @Override
    public void init(SharedDataListener controller) {
      this.updateListener = controller;
      this.channel = createChannel();
      this.consumerTag = new HashMap<>();
      initBoardRegistry();
    }

    private void initBoardRegistry() {
      Optional<Channel> regChannel = createChannel();
      declareQueue(REGISTRY_QUEUE_NAME, regChannel, DEFAULT_QUEUE_CONFIG);
      consumeMessages(REGISTRY_QUEUE_NAME, this.channel, msg -> {
        try {
          String msgBody = new String(msg.getBody(), "UTF-8");
          boardRegistry.add((JsonData) () -> msgBody);
          updateListener.newBoardCreated((JsonData) () -> msgBody);
        } catch (UnsupportedEncodingException ex) {}
      });
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
      publishTo(edits.getJsonString(), BOARD_UPDATE);
    }

    @Override
    public void updateCursor(JsonData userInfo) {
      publishTo(userInfo.getJsonString(), USER_UPDATE);
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

      Map<String, Object> args = new HashMap<>();
      args.put("x-queue-type", "stream");
      args.put("x-message-ttl", 30000);
      declareQueue(usersc, this.channel, new QueueConfigs(true, false, false, args));
      declareQueue(edits, this.channel, DEFAULT_QUEUE_CONFIG);

      consumeMessages(edits, this.channel, msg -> {
        try {
          String msgBody = new String(msg.getBody(), "UTF-8");
          //maybe here i wanna get some info from msg
          updateListener.boardUpdate((JsonData) () -> msgBody);
        } catch (UnsupportedEncodingException ex) {}
      });
      consumeMessages(usersc, this.channel, msg -> {
        try {
          String msgBody = new String(msg.getBody(), "UTF-8");
          updateListener.cursorsUpdate((JsonData) () -> msgBody);
        } catch (UnsupportedEncodingException exc) {
          this.updateListener.notifyErrors("Parsing error cursor data", exc);
        }
      });
    }

    private void declareQueue(String name, Optional<Channel> channel, QueueConfigs configs) {
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

    private void consumeMessages(String queueName, Optional<Channel> channel, Consumer<Delivery> consume) {
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
    public List<JsonData> existingBoards() {
      return List.copyOf(boardRegistry);
    }
  
}
