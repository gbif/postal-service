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

import com.rabbitmq.client.AMQP;

/**
 * Called for every new message. Implementations of this class need to be thread-safe.
 *
 * <p>This interface does not handle task abortions. Implementations are free to check their Threads
 * interrupted status but are not required to.
 *
 * @param <T> of the message to process
 */
public interface MessageCallback<T> {

  /**
   * Called every time a new message is ready to process. Exceptions being thrown are logged but
   * otherwise ignored.
   *
   * @param message to process
   */
  void handleMessage(T message);

  /** Default method to set BasicProperties, used later if needed by consumers.*/
  default void setContext(AMQP.BasicProperties properties) {
    //nothing
  }

  /** Default method to get BasicProperties, used later if needed by consumers.*/
  default AMQP.BasicProperties getContext() {
    return null;
  }

  /**
   * Returns the class of the message to be handled (i.e. the type {@code T}.
   *
   * @return class of the message to be handled
   */
  Class<T> getMessageClass();
}
