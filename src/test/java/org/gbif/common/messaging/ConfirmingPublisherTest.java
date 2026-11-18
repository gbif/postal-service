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

import org.gbif.common.messaging.api.MessagePublisher;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Return;
import com.rabbitmq.client.ReturnCallback;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Publisher settings made through {@link DefaultMessagePublisher#builder}. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ConfirmingPublisherTest {

  private static final String EXCHANGE = "orchestrator";
  private static final String ROUTING_KEY = "step.request";
  private static final Map<String, String> MESSAGE = Map.of("key", "value");

  @Mock Connection connection;
  @Mock Channel channel;

  private ConnectionParameters params;

  @BeforeEach
  void setup() throws IOException, TimeoutException {
    params = new MockConnectionParameters();
    when(params.getConnectionFactory().newConnection()).thenReturn(connection);
  }

  private DefaultMessagePublisher confirming() throws IOException {
    when(connection.createChannel()).thenReturn(channel);
    when(channel.isOpen()).thenReturn(true);
    return DefaultMessagePublisher.builder(params)
        .mapper(new ObjectMapper())
        .publisherConfirms(Duration.ofSeconds(10))
        .build();
  }

  @Test
  void waitsForConfirm() throws Exception {
    DefaultMessagePublisher publisher = confirming();

    publisher.send(MESSAGE, EXCHANGE, ROUTING_KEY);
    publisher.send(MESSAGE, EXCHANGE, ROUTING_KEY);

    // the channel is reused, so confirm mode is enabled once
    verify(channel, times(1)).confirmSelect();
    verify(channel, times(2)).waitForConfirmsOrDie(10_000L);
    verify(channel, times(2))
        .basicPublish(eq(EXCHANGE), eq(ROUTING_KEY), any(AMQP.BasicProperties.class), any(byte[].class));
  }

  @Test
  void nackFailsAfterRetries() throws Exception {
    DefaultMessagePublisher publisher = confirming();
    doThrow(new IOException("nack")).when(channel).waitForConfirmsOrDie(anyLong());

    assertThrows(IOException.class, () -> publisher.send(MESSAGE, EXCHANGE, ROUTING_KEY));

    verify(channel, times(3)).waitForConfirmsOrDie(anyLong());
  }

  @Test
  void confirmTimeoutFails() throws Exception {
    DefaultMessagePublisher publisher = confirming();
    doThrow(new TimeoutException()).when(channel).waitForConfirmsOrDie(anyLong());

    IOException e =
        assertThrows(IOException.class, () -> publisher.send(MESSAGE, EXCHANGE, ROUTING_KEY));

    assertEquals(TimeoutException.class, e.getCause().getClass());
  }

  @Test
  void returnedMessageIsUnroutableAndNotRetried() throws Exception {
    when(connection.createChannel()).thenReturn(channel);
    when(channel.isOpen()).thenReturn(true);
    AtomicReference<ReturnCallback> callback = new AtomicReference<>();
    when(channel.addReturnListener(any(ReturnCallback.class)))
        .thenAnswer(
            invocation -> {
              callback.set(invocation.getArgument(0));
              return null;
            });
    doAnswer(
            invocation -> {
              callback
                  .get()
                  .handle(
                      new Return(
                          312,
                          "NO_ROUTE",
                          EXCHANGE,
                          ROUTING_KEY,
                          invocation.getArgument(3),
                          invocation.getArgument(4)));
              return null;
            })
        .when(channel)
        .basicPublish(
            eq(EXCHANGE),
            eq(ROUTING_KEY),
            eq(true),
            any(AMQP.BasicProperties.class),
            any(byte[].class));
    DefaultMessagePublisher publisher =
        DefaultMessagePublisher.builder(params)
            .publisherConfirms(Duration.ofSeconds(1))
            .mandatory(true)
            .build();

    UnroutableMessageException e =
        assertThrows(
            UnroutableMessageException.class,
            () -> publisher.send(MESSAGE, EXCHANGE, ROUTING_KEY));

    assertEquals(312, e.getReplyCode());
    verify(channel, times(1))
        .basicPublish(
            eq(EXCHANGE),
            eq(ROUTING_KEY),
            eq(true),
            any(AMQP.BasicProperties.class),
            any(byte[].class));

    // the next routable send on the same channel is not affected by the earlier return
    doAnswer(invocation -> null)
        .when(channel)
        .basicPublish(
            eq(EXCHANGE),
            eq("routable"),
            eq(true),
            any(AMQP.BasicProperties.class),
            any(byte[].class));
    publisher.send(MESSAGE, EXCHANGE, "routable");
  }

  @Test
  void mandatoryRequiresConfirms() {
    assertThrows(
        IllegalArgumentException.class,
        () -> DefaultMessagePublisher.builder(params).mandatory(true).build());
  }

  @Test
  void sendOptionsSetProperties() throws Exception {
    DefaultMessagePublisher publisher = confirming();

    publisher.send(
        MESSAGE,
        EXCHANGE,
        ROUTING_KEY,
        SendOptions.builder()
            .persistent(true)
            .messageId("6f1c2a3e-2222-4a8b-9c0d-000000000003")
            .contentType("application/json")
            .header("workflowId", "w1")
            .build());

    ArgumentCaptor<AMQP.BasicProperties> properties =
        ArgumentCaptor.forClass(AMQP.BasicProperties.class);
    verify(channel)
        .basicPublish(eq(EXCHANGE), eq(ROUTING_KEY), properties.capture(), any(byte[].class));
    assertEquals("6f1c2a3e-2222-4a8b-9c0d-000000000003", properties.getValue().getMessageId());
    assertEquals(2, properties.getValue().getDeliveryMode());
    assertEquals("application/json", properties.getValue().getContentType());
    assertEquals("w1", properties.getValue().getHeaders().get("workflowId"));
  }

  @Test
  void sendOptionsDefaults() {
    AMQP.BasicProperties properties = SendOptions.builder().build().toProperties();

    assertEquals(1, properties.getDeliveryMode());
    assertEquals("text/plain", properties.getContentType());
    assertEquals(null, properties.getMessageId());
    assertEquals(null, properties.getHeaders());
  }

  @Test
  void declaresTargetExchangeOncePerExchange() throws Exception {
    when(connection.createChannel()).thenReturn(channel);
    when(channel.isOpen()).thenReturn(true);
    DefaultMessagePublisher publisher =
        DefaultMessagePublisher.builder(params)
            .declareRegisteredExchanges(false)
            .declareExchangeOnSend(true)
            .build();

    publisher.send(MESSAGE, EXCHANGE, ROUTING_KEY);
    publisher.send(MESSAGE, EXCHANGE, ROUTING_KEY);
    publisher.send(MESSAGE, "other", ROUTING_KEY);

    verify(channel, times(1)).exchangeDeclare(EXCHANGE, "topic", true);
    verify(channel, times(1)).exchangeDeclare("other", "topic", true);
  }

  @Test
  void noExchangesDeclaredAtStartupWhenDisabled() throws Exception {
    DefaultMessagePublisher.builder(params).declareRegisteredExchanges(false).build();

    verify(connection, never()).createChannel();
  }

  @Test
  void defaultsKeepTheOldBehaviour() throws Exception {
    when(connection.createChannel()).thenReturn(channel);
    DefaultMessagePublisher publisher = DefaultMessagePublisher.builder(params).build();

    publisher.send(MESSAGE, EXCHANGE, ROUTING_KEY);

    verify(channel, never()).confirmSelect();
    verify(channel, never()).waitForConfirmsOrDie(anyLong());
    verify(channel, never()).exchangeDeclare(eq(EXCHANGE), anyString(), anyBoolean());
    verify(channel)
        .basicPublish(eq(EXCHANGE), eq(ROUTING_KEY), any(AMQP.BasicProperties.class), any(byte[].class));
  }

  @Test
  void otherPublishersDoNotSupportSendOptions() {
    MessagePublisher stub =
        new MessagePublisher() {
          @Override
          public void send(org.gbif.common.messaging.api.Message message) {}

          @Override
          public void send(org.gbif.common.messaging.api.Message message, boolean persistent) {}

          @Override
          public void send(org.gbif.common.messaging.api.Message message, String exchange) {}

          @Override
          public void send(Object message, String exchange, String routingKey) {}

          @Override
          public void send(Object message, String exchange, String routingKey, boolean persistent) {}

          @Override
          public void replyToQueue(
              Object message, boolean persistent, String correlationId, String replyTo) {}

          @Override
          public <T> T sendAndReceive(
              org.gbif.common.messaging.api.Message message,
              String routingKey,
              boolean persistent,
              String correlationId) {
            return null;
          }

          @Override
          public <T> T sendAndReceive(
              Object message,
              String exchange,
              String routingKey,
              boolean persistent,
              String correlationId) {
            return null;
          }

          @Override
          public void close() {}
        };

    assertThrows(
        UnsupportedOperationException.class,
        () -> stub.send(MESSAGE, EXCHANGE, ROUTING_KEY, SendOptions.builder().build()));
  }
}
