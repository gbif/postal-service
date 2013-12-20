package org.gbif.common.messaging;

import org.gbif.common.messaging.api.Message;
import org.gbif.common.messaging.api.MessageRegistry;
import org.gbif.common.messaging.api.messages.CrawlStartedMessage;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.guava.api.Assertions.assertThat;

public class DefaultMessageRegistryTest {

  private MessageRegistry registry;

  @Before
  public void setup() {
    registry = new DefaultMessageRegistry();
  }

  @Test
  public void testGetExchange() {
    Optional<String> exchange = registry.getExchange(CrawlStartedMessage.class);
    assertThat(exchange).contains("crawler");
  }

  @Test
  public void testGetRoutingKey() {
    Optional<String> exchange = registry.getGenericRoutingKey(CrawlStartedMessage.class);
    assertThat(exchange).contains("crawl.started");
  }

  @Test
  public void testRegisterUnregister() {
    assertThat(registry.getExchange(TestMessage.class)).isAbsent();
    assertThat(registry.getGenericRoutingKey(TestMessage.class)).isAbsent();

    registry.register(TestMessage.class, "foo", "bar");
    Optional<String> result = registry.getExchange(TestMessage.class);
    assertThat(result).contains("foo");

    result = registry.getGenericRoutingKey(TestMessage.class);
    assertThat(result).contains("bar");

    registry.unregister(TestMessage.class);
    assertThat(registry.getExchange(TestMessage.class)).isAbsent();
    assertThat(registry.getGenericRoutingKey(TestMessage.class)).isAbsent();
  }

  @Test
  public void testGetRegisteredMessages() {
    registry.clear();

    ImmutableSet<Class<? extends Message>> messages = registry.getRegisteredMessages();
    assertThat(messages).hasSize(0);

    registry.register(TestMessage.class, "foo", "bar");
    messages = registry.getRegisteredMessages();
    assertThat(messages).hasSize(1);
    assertThat(messages).contains(TestMessage.class);
  }

  @Test
  public void testClear() {
    ImmutableSet<Class<? extends Message>> messages = registry.getRegisteredMessages();
    assertThat(messages.size()).isGreaterThan(0);

    registry.clear();
    messages = registry.getRegisteredMessages();
    assertThat(messages).hasSize(0);
  }

}
