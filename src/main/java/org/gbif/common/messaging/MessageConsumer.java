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

import org.gbif.common.messaging.api.MessageCallback;

import java.io.IOException;
import java.util.Objects;

import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * This is the consumer used by {@link MessageListener} to handle AMQP deliveries. It deserializes
 * the content and forwards it to a callback. All messages are automatically either being rejected
 * or acked.
 *
 * <p>This class is thread-safe but stateful.
 *
 * @param <T> type of message to handle, we'll try to deserialize the received content in an
 *     instance of this type
 */
@ThreadSafe
class MessageConsumer<T> extends DefaultConsumer {

  private static final Logger LOG = LoggerFactory.getLogger(MessageConsumer.class);
  private final MessageCallback<T> callback;
  private final Class<T> clazz;
  private final ObjectMapper mapper;

  MessageConsumer(
      Class<T> clazz, Channel channel, ObjectMapper mapper, MessageCallback<T> callback) {
    super(channel);
    Objects.requireNonNull(channel, "channel can't be null");

    this.clazz = Objects.requireNonNull(clazz, "clazz can't be null");
    this.mapper = Objects.requireNonNull(mapper, "mapper can't be null");
    this.callback = Objects.requireNonNull(callback, "callback can't be null");
  }

  /**
   * Delivery is being handled by deserializing the message body and creating a Runnable that's
   * being put on the ExecutorService.
   *
   * <p>If deserializing fails for any reason we reject the message and don't requeue it, apart from
   * that we ack the message no matter the outcome of the callback.
   *
   * <p>If submitting a job to the ExecutorService fails we send a rejection back to RabbitMQ.
   */
  @Override
  public void handleDelivery(
      String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
      throws IOException {
    LOG.debug("Handling delivery: [{}]", envelope.getDeliveryTag());

    T object = deserializeBody(envelope, body);
    if (object == null) {
      getChannel().basicReject(envelope.getDeliveryTag(), false);
    } else {
      handleCallback(envelope, object); // which will ACK or NACK
    }
  }

  /** This tries to deserialize the body of the message, returning the Message or null. */
  private T deserializeBody(Envelope envelope, byte[] body) throws IOException {
    T object = null;
    try {
      object = mapper.readValue(body, clazz);
    } catch (JsonMappingException e) {
      LOG.warn(
          "Could not map message. Supposed to be of type [{}]. Routing key [{}], exchange [{}]",
          clazz.getSimpleName(),
          envelope.getRoutingKey(),
          envelope.getExchange(),
          e);
    } catch (JsonParseException e) {
      LOG.warn(
          "Could not parse body of message. Supposed to be of type [{}]. Routing key [{}], exchange [{}]",
          clazz.getSimpleName(),
          envelope.getRoutingKey(),
          envelope.getExchange(),
          e);
    } catch (IOException e) {
      LOG.warn(
          "Unable to read message over network. Supposed to be of type [{}]. Routing key [{}], exchange [{}]",
          clazz.getSimpleName(),
          envelope.getRoutingKey(),
          envelope.getExchange(),
          e);
    }
    return object; // will be null on any error
  }

  private void handleCallback(Envelope envelope, T object) {
    // Handle the message and send a Nack if the Callback throws an Exception
    try {
      callback.handleMessage(object);

    } catch (Exception e) {
      LOG.warn(
          "Error handling message [{}] of type [{}]. Reject and send a nack",
          envelope.getDeliveryTag(),
          object.getClass().getSimpleName(),
          e);
      try {
        getChannel().basicNack(envelope.getDeliveryTag(), false, false);
      } catch (IOException e1) {
        LOG.warn(
            "Failed to nack message [{}] of type [{}]",
            envelope.getDeliveryTag(),
            object.getClass().getSimpleName(),
            e1);
      }
      return;
    }

    // Handling itself was successful now try to ack the message
    try {
      getChannel().basicAck(envelope.getDeliveryTag(), false);
    } catch (IOException e) {
      LOG.warn(
          "Failure acknowledging message [{}] of type [{}]",
          envelope.getDeliveryTag(),
          object.getClass().getSimpleName(),
          e);
    }
  }
}
