package org.gbif.common.messaging;

import org.gbif.common.messaging.api.Message;
import org.gbif.common.messaging.api.MessageRegistry;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Fail.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultMessagePublisherTest {

  @Mock
  Connection connection;

  @Mock
  Channel channel;

  private DefaultMessagePublisher publisher;
  private MessageRegistry registry;

  private final Message message = new TestMessage();

  private static final String DEFAULT_EXCHANGE = "foo";
  private static final String DEFAULT_ROUTINGKEY = "bar";

  private static final String TEST_EXCHANGE = "fooexchange";
  private static final String TEST_ROUTINGKEY = "barkey";

  @Before
  public void setup() throws IOException {
    registry = new DefaultMessageRegistry();
    registry.register(TestMessage.class, DEFAULT_EXCHANGE, DEFAULT_ROUTINGKEY);
    when(connection.createChannel()).thenReturn(channel);
    when(connection.isOpen()).thenReturn(true);

    ConnectionParameters params = new MockConnectionParameters();
    when(params.getConnectionFactory().newConnection()).thenReturn(connection);
    publisher = new DefaultMessagePublisher(params, registry, new ObjectMapper());
  }

  @Test
  public void testSend1() throws IOException {
    publisher.send(message);

    verify(channel).basicPublish(eq(DEFAULT_EXCHANGE),
                                 eq("foobar"),
                                 eq(MessageProperties.TEXT_PLAIN),
                                 any(byte[].class));
  }

  @Test
  public void testSend2() throws IOException {
    publisher.send(message, TEST_EXCHANGE);

    verify(channel).basicPublish(eq(TEST_EXCHANGE),
                                 eq("foobar"),
                                 eq(MessageProperties.TEXT_PLAIN),
                                 any(byte[].class));
  }

  @Test
  public void testSend3() throws IOException {
    publisher.send(message, TEST_EXCHANGE, TEST_ROUTINGKEY);

    verify(channel).basicPublish(eq(TEST_EXCHANGE),
                                 eq(TEST_ROUTINGKEY),
                                 eq(MessageProperties.TEXT_PLAIN),
                                 any(byte[].class));
  }

  @Test
  public void testFailedRetries() throws IOException {
    doThrow(new IOException()).when(channel)
      .basicPublish(anyString(), anyString(), any(AMQP.BasicProperties.class), any(byte[].class));

    try {
      publisher.send(message);
      fail("Exception should have been thrown");
    } catch (IOException e) {
      // Expected
    }

    verify(channel, times(3)).basicPublish(eq(DEFAULT_EXCHANGE),
                                           eq("foobar"),
                                           eq(MessageProperties.TEXT_PLAIN),
                                           any(byte[].class));

  }

  @Test
  public void testSuccessfulRetries() throws IOException {
    doThrow(new IOException()).doThrow(new IOException())
      .doNothing()
      .when(channel)
      .basicPublish(anyString(), anyString(), any(AMQP.BasicProperties.class), any(byte[].class));

    publisher.send(message);

    verify(channel, times(3)).basicPublish(eq(DEFAULT_EXCHANGE),
                                           eq("foobar"),
                                           eq(MessageProperties.TEXT_PLAIN),
                                           any(byte[].class));
  }
}
