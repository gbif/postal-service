/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.common.messaging;

import org.gbif.common.messaging.api.Message;
import org.gbif.common.messaging.api.MessageCallback;
import org.gbif.common.messaging.api.MessageRegistry;
import org.gbif.utils.PreconditionUtils;
import org.gbif.utils.concurrent.NamedThreadFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class MessageListener implements AutoCloseable {

  private static final Logger LOG = LoggerFactory.getLogger(MessageListener.class);
  private static final int DEFAULT_PREFETCH_COUNT = 50;
  private final int prefetchCount;
  private final ConnectionFactory connectionFactory;
  private final MessageRegistry registry;
  private final ObjectMapper mapper;
  // Keep track of connections/channels/executors created by listen(...) so we can close them
  private final List<Connection> connections = Collections.synchronizedList(new ArrayList<>());
  private final List<Channel> channels = Collections.synchronizedList(new ArrayList<>());
  private final List<ExecutorService> executors = Collections.synchronizedList(new ArrayList<>());
  // Map consumerTag -> Channel so consumers can be cancelled without closing the whole listener
  private final Map<String, Channel> consumerTagToChannel =
      Collections.synchronizedMap(new HashMap<>());
  // Map queue -> list of consumerTags so we can pause/resume per-queue
  private final Map<String, List<String>> queueToConsumerTags =
      Collections.synchronizedMap(new HashMap<>());

  /**
   * Convenience constructor that uses a default {@link ObjectMapper} and the {@link
   * DefaultMessageRegistry}.
   */
  public MessageListener(ConnectionParameters connectionParameters) throws IOException {
    this(
        connectionParameters,
        new DefaultMessageRegistry(),
        new ObjectMapper(),
        DEFAULT_PREFETCH_COUNT);
  }

  /**
   * Convenience constructor that uses a default {@link ObjectMapper} and the {@link
   * DefaultMessageRegistry} but allows setting of the prefetch count (the RabbitMQ QOS). <br>
   * The prefetch count is how many records a channel will batch consume at a time. Thus if there
   * are long running consumers, and slow message throughput, one might opt to run a low prefetch
   * count (like 1), in order to allow other channels to consume messages. This blog is worth
   * reading before opting to change the default prefetch count <a
   * href="http://www.rabbitmq.com/blog/2012/05/11/some-queuing-theory-throughput-latency-and-bandwidth/">
   * http://www.rabbitmq.com/blog/2012/05/11/some-queuing-theory-throughput-latency-and-bandwidth/</a>
   */
  public MessageListener(ConnectionParameters connectionParameters, int prefetchCount)
      throws IOException {
    this(connectionParameters, new DefaultMessageRegistry(), new ObjectMapper(), prefetchCount);
  }

  /** Builds a new MessagingService with the provided components. */
  public MessageListener(
      ConnectionParameters connectionParameters, MessageRegistry registry, ObjectMapper mapper)
      throws IOException {
    this(connectionParameters, registry, mapper, DEFAULT_PREFETCH_COUNT);
  }
  /** Builds a new MessagingService with the provided components. */
  public MessageListener(
      ConnectionParameters connectionParameters,
      MessageRegistry registry,
      ObjectMapper mapper,
      int prefetchCount)
      throws IOException {
    Objects.requireNonNull(connectionParameters, "connectionParameters can't be null");
    this.mapper = Objects.requireNonNull(mapper, "mapper can't be null");
    this.registry = Objects.requireNonNull(registry, "registry can't be null");
    PreconditionUtils.checkArgument(
        prefetchCount >= 1, "prefetchCount needs to be greater than or equal to 1");
    this.prefetchCount = prefetchCount;

    this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    LOG.info("Connecting to AMQP broker {}", connectionParameters);
    connectionFactory = connectionParameters.getConnectionFactory();
    // This ensures that the connection is valid, otherwise it'd throw an exception now
    Connection connection = null;
    try {
      connection = connectionFactory.newConnection();
    } catch (TimeoutException e) {
      throw new IOException(e);
    }
    connection.close(); // we don't store this or reuse it
  }

  /**
   * Can be used to listen for a certain type of message. This will use the default generic routing
   * key to bind to the default exchange as provided from the message registry.
   *
   * @param queue to bind to
   * @param callback to execute
   * @param <T> of the message
   * @throws IOException if there was any communication exception
   * @see #listen(String, String, String, int, MessageCallback)
   */
  public <T extends Message> void listen(
      String queue, int numberOfThreads, MessageCallback<T> callback) throws IOException {
    Objects.requireNonNull(callback, "callback can't be null");

    Optional<String> routingKey = registry.getGenericRoutingKey(callback.getMessageClass());
    PreconditionUtils.checkArgument(routingKey.isPresent(), "The message needs to be registered");

    listen(queue, routingKey.get(), numberOfThreads, callback);
  }

  /**
   * Can be used to listen for a certain type of message. This will use the provided routing key to
   * bind to the default exchange as provided from the message registry.
   *
   * @param queue to bind to
   * @param routingKey to use to bind the queue to the default exchange for this type of message
   * @param callback to execute
   * @param <T> of the message
   * @throws IOException if there was any communication exception
   * @see #listen(String, String, String, int, MessageCallback)
   */
  public <T extends Message> void listen(
      String queue, String routingKey, int numberOfThreads, MessageCallback<T> callback)
      throws IOException {
    Objects.requireNonNull(callback, "callback can't be null");

    Optional<String> exchange = registry.getExchange(callback.getMessageClass());
    PreconditionUtils.checkArgument(exchange.isPresent(), "The message needs to be registered");

    listen(queue, routingKey, exchange.get(), numberOfThreads, callback);
  }

  /**
   * Can be used to listen for a certain type of message. Prior to registering the callback it will
   * declare the exchange and queue and bind the queue to the exchange using the provided routing
   * key. The callback will be automatically invoked for each new message. Each message will be
   * acknowledged as soon as the message is delivered (not processed). Uses the default prefetch
   * count (messages to prefetch from the queue).
   *
   * @param queue to bind to
   * @param routingKey to use to bind the queue to the exchange
   * @param exchange to bind to
   * @param numberOfThreads the number of threads to use to process messages for this callback in
   *     parallel
   * @param callback to execute
   * @param <T> of the message
   * @throws IOException if there was any communication exception
   */
  public <T> void listen(
      String queue,
      String routingKey,
      String exchange,
      int numberOfThreads,
      MessageCallback<T> callback)
      throws IOException {
    Objects.requireNonNull(queue, "queue can't be null");
    Objects.requireNonNull(routingKey, "routingKey can't be null");
    Objects.requireNonNull(callback, "callback can't be empty");
    PreconditionUtils.checkArgument(
        numberOfThreads >= 1, "numberOfThreads needs to be greater than or equal to 1");

    Connection connection = null;
    Channel channel = null;
    // Create and track the executor before connecting so it is always cleaned up on failure
    ExecutorService executor =
        Executors.newFixedThreadPool(numberOfThreads, new NamedThreadFactory(queue));
    executors.add(executor);
    boolean connectionSucceeded = false;
    try {
      connection = connectionFactory.newConnection(executor);
      // track the connection for shutdown
      connections.add(connection);
      channel = connection.createChannel();
      channel.exchangeDeclare(exchange, "topic", true);
      channel.queueDeclare(queue, true, false, false, null);
      channel.queueBind(queue, exchange, routingKey);
      channel.close();
      connectionSucceeded = true;
    } catch (TimeoutException e) {
      throw new IOException(e);
    } finally {
      if (!connectionSucceeded) {
        executors.remove(executor);
        executor.shutdownNow();
      }
    }

    LOG.debug(
        "Starting to listen on exchange [{}], queue [{}] and routing key [{}] for messages of type [{}]",
        exchange,
        queue,
        routingKey,
        callback.getMessageClass().getSimpleName());

    for (int i = 0; i < numberOfThreads; i++) {
      channel = connection.createChannel();
      // track the channel so it can be closed on shutdown
      channels.add(channel);
      channel.basicQos(prefetchCount);
      String consumerTag =
          channel.basicConsume(
              queue,
              false, // autoAck disabled -> we have to manually acknowledge messages
              new MessageConsumer<T>(callback.getMessageClass(), channel, mapper, callback));
      // track consumer tag so the queue can be paused without closing the listener
      consumerTagToChannel.put(consumerTag, channel);
      synchronized (queueToConsumerTags) {
        List<String> consumerTags = queueToConsumerTags.get(queue);
        if (consumerTags == null) {
          consumerTags = Collections.synchronizedList(new ArrayList<>());
          queueToConsumerTags.put(queue, consumerTags);
        }
        consumerTags.add(consumerTag);
      }
    }
  }

  /**
   * Pause consumption from a given queue without closing the listener. This cancels the
   * consumers for that queue (they will stop receiving deliveries) but leaves connections and
   * executors in place so the queue can be resumed later.
   */
  public void pauseQueue(String queue) {
    LOG.debug("Pausing queue {}", queue);
    List<String> tags = queueToConsumerTags.remove(queue);
    if (tags == null || tags.isEmpty()) {
      LOG.debug("No consumers found for queue {} to pause", queue);
      return;
    }
    for (String tag : tags) {
      Channel ch = consumerTagToChannel.remove(tag);
      if (ch != null && ch.isOpen()) {
        try {
          ch.basicCancel(tag);
          LOG.info("Paused consumers for queue {} (consumerTag={})", queue, tag);
        } catch (Exception e) {
          LOG.warn("Failed to cancel consumer {} for queue {}", tag, queue, e);
        }
      }
    }
  }

  /**
   * Shut down the listener.
   */
  @Override
  public void close() {
    // Close channels
    synchronized (channels) {
      for (Channel ch : channels) {
        if (ch != null && ch.isOpen()) {
          try {
            ch.close();
          } catch (Exception e) {
            LOG.warn("Error closing channel", e);
          }
        }
      }
      channels.clear();
    }

    // Close connections
    synchronized (connections) {
      for (Connection conn : connections) {
        if (conn != null && conn.isOpen()) {
          try {
            conn.close();
          } catch (Exception e) {
            LOG.warn("Error closing connection", e);
          }
        }
      }
      connections.clear();
    }

    // Shutdown executors
    synchronized (executors) {
      for (ExecutorService ex : executors) {
        try {
          ex.shutdownNow();
        } catch (Exception e) {
          LOG.warn("Error shutting down executor", e);
        }
      }
      executors.clear();
    }

    // Clear consumer tracking state so this listener does not retain stale references.
    // Synchronize on both maps together (always in the same order) to clear them atomically.
    synchronized (consumerTagToChannel) {
      synchronized (queueToConsumerTags) {
        consumerTagToChannel.clear();
        queueToConsumerTags.clear();
      }
    }
  }
}
