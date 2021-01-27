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

import org.gbif.common.messaging.api.MessageRegistry;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MessageListenerTest {

  @Mock Connection connection;

  @Mock Channel channel;

  private MessageListener listener;

  private static final String DEFAULT_EXCHANGE = "foo";
  private static final String DEFAULT_ROUTINGKEY = "bar";
  private static final String QUEUE = "queue";

  @BeforeEach
  public void setup() throws IOException {
    MessageRegistry registry = new DefaultMessageRegistry();
    registry.register(TestMessage.class, DEFAULT_EXCHANGE, DEFAULT_ROUTINGKEY);
    when(connection.createChannel()).thenReturn(channel);

    ConnectionParameters params = new MockConnectionParameters();
    when(params.getConnectionFactory().newConnection(any(ExecutorService.class)))
        .thenReturn(connection);
    when(params.getConnectionFactory().newConnection()).thenReturn(connection);
    listener = new MessageListener(params, registry, new ObjectMapper());
  }

  @Test
  public void testListen1() throws IOException {
    listener.listen(QUEUE, 1, new TestMessageCallback());

    verify(channel).exchangeDeclare(DEFAULT_EXCHANGE, "topic", true);
    verify(channel).queueDeclare(QUEUE, true, false, false, null);
    verify(channel).queueBind(QUEUE, DEFAULT_EXCHANGE, DEFAULT_ROUTINGKEY);

    verify(channel).basicConsume(eq(QUEUE), eq(false), isA(Consumer.class));
  }

  @Test
  public void testListen2() throws IOException {
    String queue = "foobar";
    listener.listen(queue, "bar", 1, new TestMessageCallback());

    verify(channel).exchangeDeclare(DEFAULT_EXCHANGE, "topic", true);
    verify(channel).queueDeclare(queue, true, false, false, null);
    verify(channel).queueBind(queue, DEFAULT_EXCHANGE, DEFAULT_ROUTINGKEY);

    verify(channel).basicConsume(eq(queue), eq(false), isA(Consumer.class));
  }

  @Test
  public void testListen3() throws IOException {
    String queue = "foobar";
    String exchange = "barxchange";

    listener.listen(queue, "bar", exchange, 1, new TestMessageCallback());

    verify(channel).exchangeDeclare(exchange, "topic", true);
    verify(channel).queueDeclare(queue, true, false, false, null);
    verify(channel).queueBind(queue, exchange, DEFAULT_ROUTINGKEY);

    verify(channel).basicConsume(eq(queue), eq(false), isA(Consumer.class));
  }

  private static class TestMessageCallback extends AbstractMessageCallback<TestMessage> {

    @Override
    public void handleMessage(TestMessage message) {}
  }
}
