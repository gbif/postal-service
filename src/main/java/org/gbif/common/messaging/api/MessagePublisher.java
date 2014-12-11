package org.gbif.common.messaging.api;

import java.io.IOException;

public interface MessagePublisher {

  /**
   * Sends a message to the default exchange for this message and uses the routing key calculated from the message.
   *
   * @param message to send
   */
  void send(Message message) throws IOException;

  /**
   * Sends an optionally persistent message to the default exchange for this message and uses the routing key
   * calculated from the message.
   *
   * @param message to send
   * @param persistent whether the message should be persisted by the broker
   */
  void send(Message message, boolean persistent) throws IOException;

  /**
   * Sends a message to the given exchange using the routing key calculated from the message.
   *
   * @param message  to send
   * @param exchange to publish to
   */
  void send(Message message, String exchange) throws IOException;

  /**
   * Sends a message to the given exchange with the given routing key. Tries to reuse a channel and only opens a new
   * one if the old one was closed.
   *
   * @param message    to send. This is being converted into JSON using Jackson
   * @param exchange   to publish to
   * @param routingKey to use
   */
  void send(Object message, String exchange, String routingKey) throws IOException;

  /**
   * Sends the optionally persistent message to the given exchange with the given routing key. Tries to reuse a channel
   * and only opens a new one if the old one was closed.
   *
   * @param message    to send. This is being converted into JSON using Jackson
   * @param exchange   to publish to
   * @param routingKey to use
   * @param persistent whether the message should be persisted by the broker
   */
  void send(Object message, String exchange, String routingKey, boolean persistent) throws IOException;

  /**
   * Closes any resources used.
   */
  void close();

}
