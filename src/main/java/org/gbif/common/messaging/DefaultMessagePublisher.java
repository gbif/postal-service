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
import org.gbif.common.messaging.api.MessagePublisher;
import org.gbif.common.messaging.api.MessageRegistry;
import org.gbif.common.messaging.util.MessageUtils;
import org.gbif.utils.PreconditionUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.Return;

/** This class can be used to publish messages easily. */
@ThreadSafe
public class DefaultMessagePublisher implements MessagePublisher, Closeable {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultMessagePublisher.class);
  private static final int NUMBER_OF_RETRIES = 3;
  private final Connection connection;
  private final MessageRegistry registry;
  private final ObjectMapper mapper;
  protected String customContentType = null;

  /**
   * The pool of channels available for use. Clients will reuse available channels, but when the
   * pool is exhausted, new channels are created. This is unbounded in size and dependent on
   * consumer load (e.g. concurrent threads).
   */
  private final ConcurrentLinkedQueue<Channel> channelPool = new ConcurrentLinkedQueue<>();

  /** Null unless publisher confirms are enabled; then every send waits this long for a confirm. */
  private final Duration confirmTimeout;

  /** Whether messages are published with the mandatory flag, so unroutable ones are returned. */
  private final boolean mandatory;

  private final boolean declareExchangeOnSend;
  private final Set<String> declaredExchanges = ConcurrentHashMap.newKeySet();

  /** The last broker return per channel; only used when {@link #mandatory} is set. */
  private final Map<Channel, AtomicReference<Return>> returns = new ConcurrentHashMap<>();

  /**
   * Starts a builder for a publisher with non-default settings, such as publisher confirms or the
   * mandatory flag. Without any builder settings the result behaves like {@link
   * #DefaultMessagePublisher(ConnectionParameters, MessageRegistry, ObjectMapper)}.
   */
  public static Builder builder(ConnectionParameters connectionParameters) {
    return new Builder(connectionParameters);
  }

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
    this(new Builder(connectionParameters).registry(registry).mapper(mapper));
  }

  private DefaultMessagePublisher(Builder builder) throws IOException {
    Objects.requireNonNull(builder.connectionParameters, "connectionParameters can't be null");
    this.mapper =
        MessageUtils.configure(Objects.requireNonNull(builder.mapper, "mapper can't be null"));
    this.registry = Objects.requireNonNull(builder.registry, "registry can't be null");
    this.confirmTimeout = builder.confirmTimeout;
    this.mandatory = builder.mandatory;
    this.declareExchangeOnSend = builder.declareExchangeOnSend;

    this.mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    LOG.info("Connecting to AMQP broker {}", builder.connectionParameters);
    try {
      connection = builder.connectionParameters.getConnectionFactory().newConnection();
      if (builder.declareRegisteredExchanges) {
        declareAllExchanges(registry, connection);
      }
    } catch (TimeoutException e) {
      LOG.error("Timeout connecting to AMQP broker", e);
      throw new IOException(e);
    }
  }

  @Override
  public void send(Message message) throws IOException {
    send(message, false);
  }

  @Override
  public void send(Message message, boolean persistent) throws IOException {
    Objects.requireNonNull(message, "message can't be null");

    Optional<String> exchange = registry.getExchange(message.getClass());
    PreconditionUtils.checkArgument(exchange.isPresent(), "No exchange found for Message");
    String routingKey = message.getRoutingKey();

    send(message, exchange.get(), routingKey, persistent);
  }

  @Override
  public void send(Message message, String exchange) throws IOException {
    Objects.requireNonNull(message, "message can't be null");
    Objects.requireNonNull(exchange, "exchange can't be null");

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
    Objects.requireNonNull(message, "message can't be null");
    Objects.requireNonNull(exchange, "exchange can't be null");
    Objects.requireNonNull(routingKey, "routingKey can't be null");

    BasicProperties properties;
    if (persistent) {
      properties = MessageProperties.PERSISTENT_TEXT_PLAIN;
    } else if (customContentType != null) {
      properties = new BasicProperties.Builder().contentType(customContentType).build();
    } else {
      properties = MessageProperties.TEXT_PLAIN;
    }
    publish(message, exchange, routingKey, properties);
  }

  @Override
  public void send(Object message, String exchange, String routingKey, SendOptions options)
      throws IOException {
    Objects.requireNonNull(message, "message can't be null");
    Objects.requireNonNull(exchange, "exchange can't be null");
    Objects.requireNonNull(routingKey, "routingKey can't be null");
    Objects.requireNonNull(options, "options can't be null");
    publish(message, exchange, routingKey, options.toProperties());
  }

  private void publish(
      Object message, String exchange, String routingKey, BasicProperties properties)
      throws IOException {
    byte[] data = mapper.writeValueAsBytes(message);
    LOG.debug(
        "Sending message of type [{}] to exchange [{}] using routing key [{}]",
        message.getClass().getSimpleName(),
        exchange,
        routingKey);

    for (int attempt = 1; attempt <= NUMBER_OF_RETRIES; attempt++) {
      Channel channel = provideChannel();

      try {
        declareOnSend(channel, exchange);
        AtomicReference<Return> returned = mandatory ? returns.get(channel) : null;
        if (returned != null) {
          returned.set(null);
        }
        if (mandatory) {
          channel.basicPublish(exchange, routingKey, true, properties, data);
        } else {
          channel.basicPublish(exchange, routingKey, properties, data);
        }
        awaitConfirm(channel);
        // The broker sends basic.return before the confirm, so a return is known by now.
        Return r = returned == null ? null : returned.getAndSet(null);
        // We're not releasing this in a finally block because we assume the channel is "bad" if an
        // exception occurred
        releaseChannel(channel);
        if (r != null) {
          throw new UnroutableMessageException(
              exchange, routingKey, r.getReplyCode(), r.getReplyText());
        }
        return;
      } catch (UnroutableMessageException e) {
        throw e;
      } catch (IOException e) {
        // the channel is not reused after a failure
        returns.remove(channel);
        if (attempt >= NUMBER_OF_RETRIES) {
          LOG.warn("Tried sending message but failed {} times, aborting", attempt);
          throw e;
        }
        LOG.debug("Failed sending message, retrying");
      }
    }
  }

  private void declareOnSend(Channel channel, String exchange) throws IOException {
    if (declareExchangeOnSend && !declaredExchanges.contains(exchange)) {
      channel.exchangeDeclare(exchange, "topic", true);
      declaredExchanges.add(exchange);
    }
  }

  /** Waits for the broker confirm when confirms are enabled; a nack or timeout throws. */
  private void awaitConfirm(Channel channel) throws IOException {
    if (confirmTimeout == null) {
      return;
    }
    try {
      channel.waitForConfirmsOrDie(confirmTimeout.toMillis());
    } catch (TimeoutException e) {
      throw new IOException("No publisher confirm within " + confirmTimeout, e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new InterruptedIOException("Interrupted while waiting for a publisher confirm");
    }
  }

  @Override
  public void replyToQueue(Object message, boolean persistent, String correlationId, String replyTo)
      throws IOException {
    Objects.requireNonNull(message, "message can't be null");
    Objects.requireNonNull(correlationId, "correlationId can't be null");
    Objects.requireNonNull(replyTo, "replyTo can't be null");

    byte[] data = mapper.writeValueAsBytes(message);
    LOG.debug(
        "Sending message of type [{}] to replyTo [{}] using correlationId [{}]",
        message.getClass().getSimpleName(),
        replyTo,
        correlationId);

    for (int attempt = 1; attempt <= NUMBER_OF_RETRIES; attempt++) {
      Channel channel = provideChannel();
      try {
        AMQP.BasicProperties properties =
            getProperties(persistent).builder().correlationId(correlationId).build();
        channel.basicPublish("", replyTo, properties, data);
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

  @Override
  public <T> T sendAndReceive(
      Message message, String routingKey, boolean persistent, String correlationId)
      throws IOException, InterruptedException {
    Optional<String> exchange = registry.getExchange(message.getClass());
    PreconditionUtils.checkArgument(exchange.isPresent(), "No exchange found for Message");
    return sendAndReceive(message, exchange.get(), routingKey, persistent, correlationId);
  }

  @Override
  public <T> T sendAndReceive(
      Object message, String exchange, String routingKey, boolean persistent, String correlationId)
      throws IOException, InterruptedException {
    Objects.requireNonNull(message, "message can't be null");
    Objects.requireNonNull(exchange, "exchange can't be null");
    Objects.requireNonNull(routingKey, "routingKey can't be null");
    Objects.requireNonNull(correlationId, "correlationId can't be null");

    byte[] data = mapper.writeValueAsBytes(message);
    LOG.debug(
        "Sending message of type [{}] to exchange [{}] using routing key [{}]",
        message.getClass().getSimpleName(),
        exchange,
        routingKey);

    for (int attempt = 1; attempt <= NUMBER_OF_RETRIES; attempt++) {
      Channel channel = provideChannel();
      try {
        AMQP.BasicProperties.Builder properties = getProperties(persistent).builder();
        String replyTo = channel.queueDeclare().getQueue();
        properties.correlationId(correlationId).replyTo(replyTo);
        channel.basicPublish(exchange, routingKey, properties.build(), data);
        final BlockingQueue<byte[]> response = new ArrayBlockingQueue<>(1);
        String cTag =
            channel.basicConsume(
                replyTo,
                true,
                new DefaultConsumer(channel) {
                  @Override
                  public void handleDelivery(
                      String consumerTag,
                      Envelope envelope,
                      AMQP.BasicProperties properties,
                      byte[] body)
                      throws IOException {
                    if (properties.getCorrelationId().equals(correlationId)) {
                      response.offer(body);
                    }
                  }
                });
        // We're not releasing this in a finally block because we assume the channel is "bad" if an
        // exception occurred
        T result = mapper.readValue(response.take(), new TypeReference<T>() {});
        channel.basicCancel(cTag);
        return result;
      } catch (IOException | InterruptedException e) {
        if (attempt >= NUMBER_OF_RETRIES) {
          LOG.warn("Tried sending message but failed {} times, aborting", attempt);
          throw e;
        }
        LOG.debug("Failed sending message, retrying");
      } finally {
        releaseChannel(channel);
      }
    }
    return null;
  }

  private void declareQueue(String queueName, Channel channel) {
    try {
      channel.queueDeclare(queueName, true, false, false, new HashMap<>());
    } catch (Exception ex) {
      LOG.warn("Error declaring channel", ex);
    }
  }

  private AMQP.BasicProperties getProperties(boolean persistent) {
    return persistent ? MessageProperties.PERSISTENT_TEXT_PLAIN : MessageProperties.TEXT_PLAIN;
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
        try {
          channel.close();
        } catch (TimeoutException e) {
          throw new IOException(e);
        }
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
      if (channel != null) {
        returns.remove(channel);
      }
      LOG.debug("No pooled channels available, creating a new one");
      channel = connection.createChannel();
      if (confirmTimeout != null) {
        channel.confirmSelect();
      }
      if (mandatory) {
        AtomicReference<Return> returned = new AtomicReference<>();
        channel.addReturnListener(returned::set);
        returns.put(channel, returned);
      }
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
      returns.remove(channel);
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
      } catch (IllegalStateException e) {
        LOG.warn("IllegalStateException waiting for confirms. This is expected if confirms not enabled: "
          + e.getMessage());
        LOG.debug(e.getMessage(), e);
      } catch (IOException e) {
        LOG.warn("Exception waiting for confirms", e);
      } catch (InterruptedException e) {
        LOG.warn("Interrupted while waiting for confirms", e);
        Thread.currentThread().interrupt();
      }
    }

    try {
      LOG.info("Closing connection to AMQP broker {}", connection);
      connection.close();
    } catch (IOException e) {
      LOG.error("Exception closing connection to AMQP broker", e);
    }
  }

  /**
   * Builder for publishers with non-default settings. Unset values give the same behaviour as the
   * constructors: the {@link DefaultMessageRegistry}, a new {@link ObjectMapper}, registered
   * exchanges declared at startup, no publisher confirms and no mandatory flag.
   */
  public static final class Builder {
    private final ConnectionParameters connectionParameters;
    private MessageRegistry registry;
    private ObjectMapper mapper;
    private boolean declareRegisteredExchanges = true;
    private boolean declareExchangeOnSend;
    private Duration confirmTimeout;
    private boolean mandatory;

    private Builder(ConnectionParameters connectionParameters) {
      this.connectionParameters =
          Objects.requireNonNull(connectionParameters, "connectionParameters can't be null");
    }

    public Builder registry(MessageRegistry registry) {
      this.registry = Objects.requireNonNull(registry, "registry can't be null");
      return this;
    }

    /** The mapper used to serialize messages. Postal disables {@code FAIL_ON_EMPTY_BEANS} on it. */
    public Builder mapper(ObjectMapper mapper) {
      this.mapper = Objects.requireNonNull(mapper, "mapper can't be null");
      return this;
    }

    /**
     * Whether to declare the exchanges of all registered (annotated) messages at startup. Default
     * true, as the constructors do.
     */
    public Builder declareRegisteredExchanges(boolean declare) {
      this.declareRegisteredExchanges = declare;
      return this;
    }

    /**
     * Whether to declare the target exchange (durable topic) the first time a message is sent to
     * it. Default false.
     */
    public Builder declareExchangeOnSend(boolean declare) {
      this.declareExchangeOnSend = declare;
      return this;
    }

    /**
     * Enables publisher confirms: every send waits up to {@code timeout} for the broker to confirm
     * the message, and throws an {@link IOException} on a nack or timeout. Default off.
     */
    public Builder publisherConfirms(Duration timeout) {
      Objects.requireNonNull(timeout, "timeout can't be null");
      PreconditionUtils.checkArgument(!timeout.isNegative() && !timeout.isZero(), "timeout must be positive");
      this.confirmTimeout = timeout;
      return this;
    }

    /**
     * Publishes with the mandatory flag, so a message no queue is bound for is returned instead of
     * dropped. Requires {@link #publisherConfirms(Duration)}, which is what makes the return known
     * before the send completes; a returned message then throws {@link
     * UnroutableMessageException}. Default off.
     */
    public Builder mandatory(boolean mandatory) {
      this.mandatory = mandatory;
      return this;
    }

    public DefaultMessagePublisher build() throws IOException {
      PreconditionUtils.checkArgument(
          !mandatory || confirmTimeout != null, "mandatory requires publisherConfirms");
      if (registry == null) {
        registry = new DefaultMessageRegistry();
      }
      if (mapper == null) {
        mapper = new ObjectMapper();
      }
      return new DefaultMessagePublisher(this);
    }
  }
}
