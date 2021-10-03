/*
 * Copyright 2020 Global Biodiversity Information Facility (GBIF)
 *
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
import org.gbif.common.messaging.api.MessagePublisher;
import org.gbif.common.messaging.api.MessageRegistry;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.Queues;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/** This class can be used to publish messages easily. */
@ThreadSafe
public class DefaultMessagePublisher implements MessagePublisher, Closeable {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultMessagePublisher.class);
  private static final int NUMBER_OF_RETRIES = 3;
  private final Connection connection;
  private final MessageRegistry registry;
  private final ObjectMapper mapper;

  /**
   * The pool of channels available for use. Clients will reuse available channels, but when the
   * pool is exhausted, new channels are created. This is unbounded in size and dependent on
   * consumer load (e.g. concurrent threads).
   */
  private final ConcurrentLinkedQueue<Channel> channelPool = Queues.newConcurrentLinkedQueue();

  /**
   * Convenience constructor that uses a default {@link ObjectMapper} and the {@link
   * DefaultMessageRegistry}.
   *
   * @param connectionParameters to use
   * @see #DefaultMessagePublisher(ConnectionParameters, MessageRegistry, ObjectMapper)
   */
  public DefaultMessagePublisher(ConnectionParameters connectionParameters) throws IOException {
    this(connectionParameters, new DefaultMessageRegistry(), new ObjectMapper());
  }

  /**
   * Builds a new MessagingService with the provided components, and declares all the necessary
   * exchanges as topic based.
   *
   * @param connectionParameters to use
   * @param registry to use
   * @param mapper to use
   */
  public DefaultMessagePublisher(
      ConnectionParameters connectionParameters, MessageRegistry registry, ObjectMapper mapper)
      throws IOException {
    checkNotNull(connectionParameters, "connectionParameters can't be null");
    this.mapper = checkNotNull(mapper, "mapper can't be null");
    this.registry = checkNotNull(registry, "registry can't be null");

    this.mapper.registerModule(new GuavaModule());
    this.mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    LOG.info("Connecting to AMQP broker {}", connectionParameters);
    connection = connectionParameters.getConnectionFactory().newConnection();
    declareAllExchanges(registry, connection);
  }

  @Override
  public void send(Message message) throws IOException {
    send(message, false);
  }

  @Override
  public void send(Message message, boolean persistent) throws IOException {
    checkNotNull(message, "message can't be null");

    Optional<String> exchange = registry.getExchange(message.getClass());
    checkArgument(exchange.isPresent(), "No exchange found for Message");
    String routingKey = message.getRoutingKey();

    send(message, exchange.get(), routingKey, persistent);
  }

  @Override
  public void send(Message message, String exchange) throws IOException {
    checkNotNull(message, "message can't be null");
    checkNotNull(exchange, "exchange can't be null");

    String routingKey = message.getRoutingKey();
    send(message, exchange, routingKey);
  }

  @Override
  public void send(Object message, String exchange, String routingKey) throws IOException {
    send(message, exchange, routingKey, false);
  }

  @Override
  public void send(Object message, String exchange, String routingKey, boolean persistent)
      throws IOException {
    checkNotNull(message, "message can't be null");
    checkNotNull(exchange, "exchange can't be null");
    checkNotNull(routingKey, "routingKey can't be null");

    byte[] data = mapper.writeValueAsBytes(message);
    LOG.debug(
        "Sending message of type [{}] to exchange [{}] using routing key [{}]",
        message.getClass().getSimpleName(),
        exchange,
        routingKey);

    for (int attempt = 1; attempt <= NUMBER_OF_RETRIES; attempt++) {
      Channel channel = provideChannel();

      try {
        if (persistent) {
          channel.basicPublish(exchange, routingKey, MessageProperties.PERSISTENT_TEXT_PLAIN, data);
        } else {
          channel.basicPublish(exchange, routingKey, MessageProperties.TEXT_PLAIN, data);
        }
        // We're not releasing this in a finally block because we assume the channel is "bad" if an
        // exception occurred
        releaseChannel(channel);
        return;
      } catch (IOException e) {
        if (attempt >= NUMBER_OF_RETRIES) {
          LOG.warn("Tried sending message but failed {} times, aborting", attempt);
          throw e;
        }
        LOG.debug("Failed sending message, retrying");
      }
    }
  }

  /**
   * Declares all exchanges used by the registry as <em>topic</em> exchanges.
   *
   * @param registry to get the registered messages and exchanges from
   * @param connection used to declare the exchanges
   * @throws IOException if there was a problem declaring an exchange. This might leave the
   *     exchanges partially declared
   */
  private void declareAllExchanges(MessageRegistry registry, Connection connection)
      throws IOException {
    Channel channel = null;
    try {
      channel = connection.createChannel();
      for (Class<? extends Message> message : registry.getRegisteredMessages()) {
        channel.exchangeDeclare(registry.getExchange(message).get(), "topic", true);
      }
    } finally {
      if (channel != null) {
        channel.close();
      }
    }
  }

  /**
   * Callers must return the channel using {@link #releaseChannel(Channel channel) releaseChannel}.
   *
   * @return a channel to reuse, or a new one if none are available
   */
  private Channel provideChannel() throws IOException {
    Channel channel = channelPool.poll();
    if (channel == null || !channel.isOpen()) { // none for reuse
      LOG.debug("No pooled channels available, creating a new one");
      channel = connection.createChannel();
    }
    return channel;
  }

  /**
   * Returns the channels to the pool for reuse.
   *
   * @param channel To free up for another use
   */
  private void releaseChannel(Channel channel) {
    // only reuse open channels
    if (channel.isOpen()) {
      channelPool.add(channel);
      LOG.debug(
          "Channel returned to the pool. Available channels for reuse: {}", channelPool.size());
    } else {
      LOG.debug(
          "Discarding channel since it is closed. Available channels for reuse: {}",
          channelPool.size());
    }
  }

  /** Close connections, which waits for queued batches of messages to be delivered. */
  @Override
  public void close() {
    // connection.close() is sufficient for queued messages to be delivered, but this would
    // better log any failures.
    for (Channel c : channelPool) {
      try {
        c.waitForConfirmsOrDie();
      } catch (IOException | InterruptedException e) {
        LOG.warn("Exception waiting for confirms", e);
      }
    }

    try {
      LOG.info("Closing connection to AMQP broker {}", connection);
      connection.close();
    } catch (IOException e) {
      LOG.error("Exception closing connection to AMQP broker", e);
    }
  }
}
