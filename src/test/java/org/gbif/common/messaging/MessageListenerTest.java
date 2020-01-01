package org.gbif.common.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.gbif.common.messaging.api.MessageRegistry;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessageListenerTest {

  @Mock
  Connection connection;

  @Mock
  Channel channel;

  private MessageListener listener;

  private static final String DEFAULT_EXCHANGE = "foo";
  private static final String DEFAULT_ROUTINGKEY = "bar";
  private static final String QUEUE = "queue";

  @Before
  public void setup() throws IOException {
    MessageRegistry registry = new DefaultMessageRegistry();
    registry.register(TestMessage.class, DEFAULT_EXCHANGE, DEFAULT_ROUTINGKEY);
    when(connection.createChannel()).thenReturn(channel);
    when(connection.isOpen()).thenReturn(true);

    ConnectionParameters params = new MockConnectionParameters();
    when(params.getConnectionFactory().newConnection(any(ExecutorService.class))).thenReturn(connection);
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
    public void handleMessage(TestMessage message) {

    }

  }

}
