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

import org.gbif.common.messaging.api.Message;
import org.gbif.common.messaging.api.MessageRegistry;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultMessagePublisherTest {

  @Mock Connection connection;

  @Mock Channel channel;

  private DefaultMessagePublisher publisher;
  private MessageRegistry registry;

  private final Message message = new TestMessage();

  private static final String DEFAULT_EXCHANGE = "foo";
  private static final String DEFAULT_ROUTINGKEY = "bar";

  private static final String TEST_EXCHANGE = "fooexchange";
  private static final String TEST_ROUTINGKEY = "barkey";

  @BeforeEach
  public void setup() throws IOException {
    registry = new DefaultMessageRegistry();
    registry.register(TestMessage.class, DEFAULT_EXCHANGE, DEFAULT_ROUTINGKEY);
    when(connection.createChannel()).thenReturn(channel);

    ConnectionParameters params = new MockConnectionParameters();
    when(params.getConnectionFactory().newConnection()).thenReturn(connection);
    publisher = new DefaultMessagePublisher(params, registry, new ObjectMapper());
  }

  @Test
  public void testSend1() throws IOException {
    publisher.send(message);

    verify(channel)
        .basicPublish(
            eq(DEFAULT_EXCHANGE),
            eq("foobar"),
            eq(MessageProperties.TEXT_PLAIN),
            any(byte[].class));
  }

  @Test
  public void testSend2() throws IOException {
    publisher.send(message, TEST_EXCHANGE);

    verify(channel)
        .basicPublish(
            eq(TEST_EXCHANGE), eq("foobar"), eq(MessageProperties.TEXT_PLAIN), any(byte[].class));
  }

  @Test
  public void testSend3() throws IOException {
    publisher.send(message, TEST_EXCHANGE, TEST_ROUTINGKEY);

    verify(channel)
        .basicPublish(
            eq(TEST_EXCHANGE),
            eq(TEST_ROUTINGKEY),
            eq(MessageProperties.TEXT_PLAIN),
            any(byte[].class));
  }

  @Test
  public void testPersistentSend1() throws IOException {
    publisher.send(message, true);

    verify(channel)
        .basicPublish(
            eq(DEFAULT_EXCHANGE),
            eq("foobar"),
            eq(MessageProperties.PERSISTENT_TEXT_PLAIN),
            any(byte[].class));
  }

  @Test
  public void testPersistentSend2() throws IOException {
    publisher.send(message, TEST_EXCHANGE, TEST_ROUTINGKEY, true);

    verify(channel)
        .basicPublish(
            eq(TEST_EXCHANGE),
            eq(TEST_ROUTINGKEY),
            eq(MessageProperties.PERSISTENT_TEXT_PLAIN),
            any(byte[].class));
  }

  @Test
  public void testFailedRetries() throws IOException {
    doThrow(new IOException())
        .when(channel)
        .basicPublish(anyString(), anyString(), any(AMQP.BasicProperties.class), any(byte[].class));

    assertThrows(IOException.class, () -> publisher.send(message));

    verify(channel, times(3))
        .basicPublish(
            eq(DEFAULT_EXCHANGE),
            eq("foobar"),
            eq(MessageProperties.TEXT_PLAIN),
            any(byte[].class));
  }

  @Test
  public void testSuccessfulRetries() throws IOException {
    doThrow(new IOException())
        .doThrow(new IOException())
        .doNothing()
        .when(channel)
        .basicPublish(anyString(), anyString(), any(AMQP.BasicProperties.class), any(byte[].class));

    publisher.send(message);

    verify(channel, times(3))
        .basicPublish(
            eq(DEFAULT_EXCHANGE),
            eq("foobar"),
            eq(MessageProperties.TEXT_PLAIN),
            any(byte[].class));
  }
}
