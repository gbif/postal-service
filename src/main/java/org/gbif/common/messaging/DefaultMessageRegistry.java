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
import org.gbif.common.messaging.api.MessageRegistry;
import org.gbif.common.messaging.api.messages.*;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.reflections.Reflections;

import lombok.extern.slf4j.Slf4j;

/**
 * A default implementation of the {@link MessageRegistry} interface where all the messages from
 * this projects {@code org.gbif.common.messaging.api} package are already preregistered.
 *
 * <p>This class is thread-safe.
 */
@Slf4j
@ThreadSafe
public class DefaultMessageRegistry implements MessageRegistry {

  private static final String MESSAGES_PACKAGE = "org.gbif.common.messaging.api.messages";

  private final Object lock = new Object();

  @GuardedBy("lock")
  private final ConcurrentMap<Class<? extends Message>, String> exchangeMapping =
      new ConcurrentHashMap<>();

  @GuardedBy("lock")
  private final ConcurrentMap<Class<? extends Message>, String> routingKeyMapping =
      new ConcurrentHashMap<>();

  public DefaultMessageRegistry() {
    Reflections reflections = new Reflections(MESSAGES_PACKAGE);
    Set<Class<? extends Message>> messageClasses = reflections.getSubTypesOf(Message.class);

    for (Class<? extends Message> messageClass : messageClasses) {
      MessageBinding binding = messageClass.getAnnotation(MessageBinding.class);
      if (binding != null) {
        exchangeMapping.put(messageClass, binding.exchange().getValue());
        routingKeyMapping.put(messageClass, binding.routingKey());
      } else {
        log.warn("Message class {} has no @MessageBinding annotation and will not be registered",
            messageClass.getName());
      }
    }
  }

  @Override
  @GuardedBy("lock")
  public Optional<String> getExchange(Class<? extends Message> message) {
    Objects.requireNonNull(message, "message can't be null");

    return Optional.ofNullable(exchangeMapping.get(message));
  }

  @GuardedBy("lock")
  @Override
  public Optional<String> getGenericRoutingKey(Class<? extends Message> message) {
    Objects.requireNonNull(message, "message can't be null");

    return Optional.ofNullable(routingKeyMapping.get(message));
  }

  @Override
  public Set<Class<? extends Message>> getRegisteredMessages() {
    synchronized (lock) {
      return Collections.unmodifiableSet(exchangeMapping.keySet());
    }
  }

  /**
   * Used to register a new message or change information about an existing one. No special care is
   * taken to protect the default set of messages.
   *
   * @param message to register
   * @param exchange it uses
   * @param routingKey it uses
   */
  @Override
  public void register(Class<? extends Message> message, String exchange, String routingKey) {
    Objects.requireNonNull(message, "message can't be null");
    Objects.requireNonNull(exchange, "exchange can't be null");
    Objects.requireNonNull(routingKey, "routingKey can't be null");

    synchronized (lock) {
      exchangeMapping.put(message, exchange);
      routingKeyMapping.put(message, routingKey);
    }
  }

  /**
   * Deletes information about a message from the registry. No special care is taken to protect the
   * default set of messages.
   *
   * @param message to unregister
   */
  @Override
  public void unregister(Class<? extends Message> message) {
    Objects.requireNonNull(message, "message can't be null");

    synchronized (lock) {
      exchangeMapping.remove(message);
      routingKeyMapping.remove(message);
    }
  }

  /**
   * Deletes information about all message from the registry. No special care is taken to protect
   * the default set of messages.
   */
  @Override
  public void clear() {
    synchronized (lock) {
      exchangeMapping.clear();
      routingKeyMapping.clear();
    }
  }
}
