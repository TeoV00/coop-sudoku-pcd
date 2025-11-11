package pcd.ass3.sudoku.mom;

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

import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Delivery;

public class StreamRabbitDistributor implements DataDistributor {

  private record QueueConfigs(
    boolean durable, 
    boolean notExclusive, 
    boolean notAutoDelete, 
    Map<String,Object> params
  ){};

  private final String REGISTRY_QUEUE_NAME = "board-registry";
  private final List<JsonData> boardRegistry = new ArrayList<>();
  private final String BOARD_UPDATE = "edits";
  private final String USER_UPDATE = "user-cursors";
  private static final int PREFETCH_COUNT = 200;
  private final static String FIRST_STREAM_OFFSET = "first";
  private final String initial_cursors_offset = "last";

  private SharedDataListener updateListener;
  private Optional<String> boardName;
  private Optional<Channel> channel;
   private Optional<Channel> registryChannel;
  private Map<String, Optional<String>> consumerTag;
  private final QueueConfigs DURABLE_STREAM_CONFIG = new QueueConfigs(true, false, false, Collections.singletonMap("x-queue-type", "stream"));

    @Override
    public void init(SharedDataListener controller) {
      this.updateListener = controller;
      this.channel = createChannel();
      this.consumerTag = new HashMap<>();
      initBoardRegistry();
    }

    private void initBoardRegistry() {
      this.registryChannel = createChannel();
      declareQueue(REGISTRY_QUEUE_NAME, this.registryChannel, DURABLE_STREAM_CONFIG);
      consumeMessages(REGISTRY_QUEUE_NAME, this.channel, msg -> {
        try {
          String msgBody = new String(msg.getBody(), "UTF-8");
          boardRegistry.add((JsonData) () -> msgBody);
          updateListener.newBoardCreated((JsonData) () -> msgBody);
        } catch (UnsupportedEncodingException ex) {}
      }, FIRST_STREAM_OFFSET);
    }

    @Override
    public void registerBoard(JsonData data) {
      publishTo(data.getJsonString(), REGISTRY_QUEUE_NAME, this.registryChannel);
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
      publishTo(edits.getJsonString(), concat(boardName.orElse("unknown"), BOARD_UPDATE), this.channel);
    }

    @Override
    public void updateCursor(JsonData userInfo) {
      publishTo(userInfo.getJsonString(), concat(boardName.orElse("unknown"), USER_UPDATE), this.channel);
    }

    @Override
    public void subscribe(String boardName) {
      this.boardName = Optional.of(boardName);
      String edits = concat(boardName, BOARD_UPDATE);
      String usersc = concat(boardName, USER_UPDATE);

      Map<String, Object> args = new HashMap<>();
      args.put("x-queue-type", "stream");
      args.put("max-age", "1m");
      args.put("x-stream-max-segment-size-bytes", 1048576); // 1MB

      var userOk = declareQueue(usersc, this.channel, new QueueConfigs(true, false, false, args));
      var editsOk = declareQueue(edits, this.channel, DURABLE_STREAM_CONFIG);

      if (userOk.isPresent() && editsOk.isPresent()) {
        consumeMessages(edits, this.channel, msg -> {
          try {
            String msgBody = new String(msg.getBody(), "UTF-8");
            //maybe here i wanna get some info from msg
            updateListener.boardUpdate((JsonData) () -> msgBody);
          } catch (UnsupportedEncodingException ex) {}
        }, FIRST_STREAM_OFFSET);

        consumeMessages(usersc, this.channel, msg -> {
          try {
            String msgBody = new String(msg.getBody(), "UTF-8");
            updateListener.cursorsUpdate((JsonData) () -> msgBody);
          } catch (UnsupportedEncodingException exc) {
            this.updateListener.notifyErrors("Parsing error cursor data", exc);
          }
        }, initial_cursors_offset);
        updateListener.joined();
      }
    }

    private void publishTo(String jsonMsg, String queueName, Optional<Channel> channel) {
      channel.ifPresent(c -> {
        try {
          c.basicPublish("", queueName, null, jsonMsg.getBytes("UTF-8"));
        } catch (IOException exc) {
          String errMsg = "Error in publishing on queue" + queueName;
          this.updateListener.notifyErrors(errMsg , exc);
        }
      });
    }

    private String concat(String name, String queueName) {
      return String.join("-", name, queueName);
    }

    private Optional<DeclareOk> declareQueue(String name, Optional<Channel> channel, QueueConfigs configs) {
      if (channel.isPresent()) {
        try {
          DeclareOk isOk = channel.get().queueDeclare(name,
                  configs.durable,
                  configs.notExclusive,
                  configs.notAutoDelete,
                  configs.params);
          return Optional.of(isOk);
        } catch (IOException exc) {
          this.updateListener.notifyErrors("Declaration queue", exc);
        }
      }
      return Optional.empty();
    }

    private void consumeMessages(String queueName, Optional<Channel> channel, Consumer<Delivery> consume, String consumeOffset) {
      Optional<String> tag = Optional.empty();
      if (channel.isPresent()) {
        Channel ch = channel.get();
        boolean autoAck = false;
        try {
          ch.basicQos(PREFETCH_COUNT);
          String cTag = ch.basicConsume(queueName,
            autoAck,
            Collections.singletonMap("x-stream-offset", consumeOffset),
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
    public void unsubscribe() {
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
