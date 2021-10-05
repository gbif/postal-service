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
package org.gbif.common.messaging;

import org.gbif.common.messaging.api.Message;
import org.gbif.common.messaging.api.MessageRegistry;
import org.gbif.common.messaging.api.messages.BackboneChangedMessage;
import org.gbif.common.messaging.api.messages.CrawlStartedMessage;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import com.google.common.collect.ImmutableSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DefaultMessageRegistryTest {

  private MessageRegistry registry;

  @BeforeEach
  public void setup() {
    registry = new DefaultMessageRegistry();
  }

  @Test
  public void testGetMessageRegistration() {
    Reflections reflections = new Reflections(BackboneChangedMessage.class.getPackage().getName());
    for (Class<? extends Message> msgClass : reflections.getSubTypesOf(Message.class)) {
      if (msgClass.isInterface()) {
        continue;
      }
      System.out.println(msgClass);

      Optional<String> exchange = registry.getExchange(msgClass);
      assertTrue(exchange.isPresent(), "exchange missing for " + msgClass);

      Optional<String> routing = registry.getGenericRoutingKey(msgClass);
      assertTrue(routing.isPresent(), "routing missing for " + msgClass);
    }
  }

  @Test
  public void testGetExchange() {
    Optional<String> exchange = registry.getExchange(CrawlStartedMessage.class);
    assertTrue(exchange.isPresent());
    assertTrue(exchange.get().contains("crawler"));
  }

  @Test
  public void testGetRoutingKey() {
    Optional<String> routing = registry.getGenericRoutingKey(CrawlStartedMessage.class);
    assertTrue(routing.isPresent());
    assertTrue(routing.get().contains("crawl.started"));
  }

  @Test
  public void testRegisterUnregister() {
    assertFalse(registry.getExchange(TestMessage.class).isPresent());
    assertFalse(registry.getGenericRoutingKey(TestMessage.class).isPresent());

    registry.register(TestMessage.class, "foo", "bar");
    Optional<String> result = registry.getExchange(TestMessage.class);
    assertTrue(result.isPresent());
    assertTrue(result.get().contains("foo"));

    result = registry.getGenericRoutingKey(TestMessage.class);
    assertTrue(result.isPresent());
    assertTrue(result.get().contains("bar"));

    registry.unregister(TestMessage.class);
    assertFalse(registry.getExchange(TestMessage.class).isPresent());
    assertFalse(registry.getGenericRoutingKey(TestMessage.class).isPresent());
  }

  @Test
  public void testGetRegisteredMessages() {
    registry.clear();

    ImmutableSet<Class<? extends Message>> messages = registry.getRegisteredMessages();
    assertEquals(0, messages.size());

    registry.register(TestMessage.class, "foo", "bar");
    messages = registry.getRegisteredMessages();
    assertEquals(1, messages.size());
    assertTrue(messages.contains(TestMessage.class));
  }

  @Test
  public void testClear() {
    ImmutableSet<Class<? extends Message>> messages = registry.getRegisteredMessages();
    assertTrue(messages.size() > 0);

    registry.clear();
    messages = registry.getRegisteredMessages();
    assertEquals(0, messages.size());
  }
}
