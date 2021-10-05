/*
 * Copyright 2021 Global Biodiversity Information Facility (GBIF)
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

import java.util.Optional;

import com.google.common.collect.ImmutableSet;

/** Used to register default exchanges and routing keys for Messages. */
public interface MessageRegistry {

  /**
   * Gets the default exchange used for this type of message.
   *
   * @param message to get the exchange for
   * @return an Optional containing a exchange name if it is available
   */
  Optional<String> getExchange(Class<? extends Message> message);

  /**
   * Gets a routing key that can be used to receive all messages of this type no matter if they are
   * using a more specialized routing key or not.
   *
   * <p>Example: We could use one message class to announce new or updated occurrences. They could
   * use the routing keys {@code occurrence.new} and {@code occurrence.updated} respectively. This
   * method could return {@code occurrence.*}.
   *
   * @param message to get the generic routing key for
   * @return an Optional containing a routing key that can be used to bind a queue to a topic
   *     exchange
   */
  Optional<String> getGenericRoutingKey(Class<? extends Message> message);

  /**
   * Returns an immutable set of all registered message classes.
   *
   * @return immutable set of registered message classes
   */
  ImmutableSet<Class<? extends Message>> getRegisteredMessages();

  /**
   * Registers a new message in this registry. If any information did already exist about this
   * message we overwrite it.
   *
   * @param message to register
   * @param exchange it uses
   * @param routingKey it uses
   */
  void register(Class<? extends Message> message, String exchange, String routingKey);

  /**
   * Deletes any information this registry might have about a message type. We ignore this request
   * if we don't have any information about the message.
   *
   * @param message to unregister
   */
  void unregister(Class<? extends Message> message);

  /** Unregisters all currently registered messages. */
  void clear();
}
