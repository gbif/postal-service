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
package org.gbif.common.messaging.api;

import java.io.IOException;

public interface MessagePublisher {

  /**
   * Sends a message to the default exchange for this message and uses the routing key calculated
   * from the message.
   *
   * @param message to send
   */
  void send(Message message) throws IOException;

  /**
   * Sends an optionally persistent message to the default exchange for this message and uses the
   * routing key calculated from the message.
   *
   * @param message to send
   * @param persistent whether the message should be persisted by the broker
   */
  void send(Message message, boolean persistent) throws IOException;

  /**
   * Sends a message to the given exchange using the routing key calculated from the message.
   *
   * @param message to send
   * @param exchange to publish to
   */
  void send(Message message, String exchange) throws IOException;

  /**
   * Sends a message to the given exchange with the given routing key. Tries to reuse a channel and
   * only opens a new one if the old one was closed.
   *
   * @param message to send. This is being converted into JSON using Jackson
   * @param exchange to publish to
   * @param routingKey to use
   */
  void send(Object message, String exchange, String routingKey) throws IOException;

  /**
   * Sends the optionally persistent message to the given exchange with the given routing key. Tries
   * to reuse a channel and only opens a new one if the old one was closed.
   *
   * @param message to send. This is being converted into JSON using Jackson
   * @param exchange to publish to
   * @param routingKey to use
   * @param persistent whether the message should be persisted by the broker
   */
  void send(Object message, String exchange, String routingKey, boolean persistent)
      throws IOException;


  /**
   * Sends a message to a reply queue. Tries to reuse a channel and only opens a new one if the old one was closed.
   *
   * @param message to send. This is being converted into JSON using Jackson
   * @param persistent whether the message should be persisted by the broker
   * @param correlationId used to correspond RPC messages with requests
   * @param replyTo callback queue
   */
  void replyToQueue(Object message, boolean persistent, String correlationId, String replyTo)
    throws IOException;

  /**
   * Sends and wait for reply of an optionally persistent message to the given exchange with the given routing key. Tries
   * to reuse a channel and only opens a new one if the old one was closed.
   *
   * @param message to send. This is being converted into JSON using Jackson
   * @param exchange to publish to
   * @param routingKey to use
   * @param persistent whether the message should be persisted by the broker
   * @param correlationId used to correspond RPC messages with requests
   * @param replyTo callback queue
   * @param consumer reply message consumer
   */
  <T> void sendAndReceive(Object message, String exchange, String routingKey, boolean persistent,
                                 String correlationId, String replyTo, java.util.function.Consumer<T> consumer) throws IOException;

  /** Closes any resources used. */
  void close();
}
