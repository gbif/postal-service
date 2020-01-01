package org.gbif.common.messaging;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.base.Optional;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.gbif.common.messaging.api.Message;
import org.gbif.common.messaging.api.MessageCallback;
import org.gbif.common.messaging.api.MessageRegistry;
import org.gbif.utils.concurrent.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class MessageListener implements AutoCloseable {

  private static final Logger LOG = LoggerFactory.getLogger(MessageListener.class);
  private static final int DEFAULT_PREFETCH_COUNT = 50;
  private final int prefetchCount;
  private final ConnectionFactory connectionFactory;
  private final MessageRegistry registry;
  private final ObjectMapper mapper;

  /**
   * Convenience constructor that uses a default {@link ObjectMapper} and the {@link DefaultMessageRegistry}.
   */
  public MessageListener(ConnectionParameters connectionParameters) throws IOException {
    this(connectionParameters, new DefaultMessageRegistry(), new ObjectMapper(), DEFAULT_PREFETCH_COUNT);
  }

  /**
   * Convenience constructor that uses a default {@link ObjectMapper} and the {@link DefaultMessageRegistry} but allows
   * setting of the prefetch count (the RabbitMQ QOS).
   * <br/>
   * The prefetch count is how many records a channel will batch consume at a time.  Thus if there are long running
   * consumers, and slow message throughput, one might opt to run a low prefetch count (like 1), in order to allow
   * other channels to consume messages.  This blog is worth reading before opting to change the default prefetch count
   * <a href="http://www.rabbitmq.com/blog/2012/05/11/some-queuing-theory-throughput-latency-and-bandwidth/">
   * http://www.rabbitmq.com/blog/2012/05/11/some-queuing-theory-throughput-latency-and-bandwidth/</a>
   */
  public MessageListener(ConnectionParameters connectionParameters, int prefetchCount) throws IOException {
    this(connectionParameters, new DefaultMessageRegistry(), new ObjectMapper(), prefetchCount);
  }

  /**
   * Builds a new MessagingService with the provided components.
   */
  public MessageListener(ConnectionParameters connectionParameters, MessageRegistry registry, ObjectMapper mapper) throws IOException {
    this(connectionParameters, registry, mapper, DEFAULT_PREFETCH_COUNT);
  }
  /**
   * Builds a new MessagingService with the provided components.
   */
  public MessageListener(ConnectionParameters connectionParameters, MessageRegistry registry, ObjectMapper mapper, int prefetchCount)
    throws IOException {
    checkNotNull(connectionParameters, "connectionParameters can't be null");
    this.mapper = checkNotNull(mapper, "mapper can't be null");
    this.registry = checkNotNull(registry, "registry can't be null");
    checkArgument(prefetchCount >= 1, "prefetchCount needs to be greater than or equal to 1");
    this.prefetchCount = prefetchCount;

    this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    LOG.info("Connecting to AMQP broker {}", connectionParameters);
    connectionFactory = connectionParameters.getConnectionFactory();
    // This ensures that the connection is valid, otherwise it'd throw an exception now
    Connection connection = connectionFactory.newConnection();
    connection.close(); // we don't store this or reuse it
    mapper.registerModule(new GuavaModule());
  }

  /**
   * Can be used to listen for a certain type of message. This will use the default generic routing key to bind to the
   * default exchange as provided from the message registry.
   *
   * @param queue    to bind to
   * @param callback to execute
   * @param <T>      of the message
   *
   * @throws IOException if there was any communication exception
   * @see #listen(String, String, String, int, MessageCallback)
   */
  public <T extends Message> void listen(String queue, int numberOfThreads, MessageCallback<T> callback)
    throws IOException {
    checkNotNull(callback, "callback can't be null");

    Optional<String> routingKey = registry.getGenericRoutingKey(callback.getMessageClass());
    checkArgument(routingKey.isPresent(), "The message needs to be registered");

    listen(queue, routingKey.get(), numberOfThreads, callback);
  }

  /**
   * Can be used to listen for a certain type of message. This will use the provided routing key to bind to the
   * default exchange as provided from the message registry.
   *
   * @param queue      to bind to
   * @param routingKey to use to bind the queue to the default exchange for this type of message
   * @param callback   to execute
   * @param <T>        of the message
   *
   * @throws IOException if there was any communication exception
   * @see #listen(String, String, String, int, MessageCallback)
   */
  public <T extends Message> void listen(String queue, String routingKey, int numberOfThreads,
    MessageCallback<T> callback) throws IOException {
    checkNotNull(callback, "callback can't be null");

    Optional<String> exchange = registry.getExchange(callback.getMessageClass());
    checkArgument(exchange.isPresent(), "The message needs to be registered");

    listen(queue, routingKey, exchange.get(), numberOfThreads, callback);
  }

  /**
   * Can be used to listen for a certain type of message. Prior to registering the callback it will declare the
   * exchange and queue and bind the queue to the exchange using the provided routing key.
   * The callback will be automatically invoked for each new message. Each message will be acknowledged as soon as the
   * message is delivered (not processed). Uses the default prefetch count (messages to prefetch from the queue).
   *
   * @param queue           to bind to
   * @param routingKey      to use to bind the queue to the exchange
   * @param exchange        to bind to
   * @param numberOfThreads the number of threads to use to process messages for this callback in parallel
   * @param callback        to execute
   * @param <T>             of the message
   *
   * @throws IOException if there was any communication exception
   */
  public <T> void listen(String queue, String routingKey, String exchange, int numberOfThreads,
    MessageCallback<T> callback) throws IOException {
    checkNotNull(queue, "queue can't be null");
    checkNotNull(routingKey, "routingKey can't be null");
    checkNotNull(callback, "callback can't be empty");
    checkArgument(numberOfThreads >= 1, "numberOfThreads needs to be greater than or equal to 1");

    Connection connection = connectionFactory.newConnection(
        Executors.newFixedThreadPool(numberOfThreads, new NamedThreadFactory(queue))
    );
    Channel channel = connection.createChannel();
    channel.exchangeDeclare(exchange, "topic", true);
    channel.queueDeclare(queue, true, false, false, null);
    channel.queueBind(queue, exchange, routingKey);
    channel.close();

    LOG
      .debug("Starting to listen on exchange [{}], queue [{}] and routing key [{}] for messages of type [{}]", exchange,
        queue, routingKey, callback.getMessageClass().getSimpleName());

    for (int i = 0; i < numberOfThreads; i++) {
      channel = connection.createChannel();
      channel.basicQos(prefetchCount);
      channel.basicConsume(queue, false, // autoAck disabled -> we have to manually acknowledge messages
        new MessageConsumer<T>(callback.getMessageClass(), channel, mapper, callback));
    }
  }

  /**
   * Does nothing, but adding for potential future use.
   */
  public void close() {

  }
}
