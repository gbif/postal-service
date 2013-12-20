package org.gbif.common.messaging;

import org.gbif.common.messaging.api.MessagePublisher;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ForwardingMessagePublisherTest {

  private final TestMessage message = new TestMessage();

  @Mock
  private MessagePublisher mockPublisher;

  @Test
  public void testForwarding1() throws IOException {
    MessagePublisher publisher = new ForwardingMessagePublisher(mockPublisher);
    publisher.send(message);
    verify(mockPublisher).send(message);
  }

  @Test
  public void testForwarding2() throws IOException {
    MessagePublisher publisher = new ForwardingMessagePublisher(mockPublisher);
    publisher.send(message, "foobar");
    verify(mockPublisher).send(message, "foobar");
  }

  @Test
  public void testForwarding3() throws IOException {
    MessagePublisher publisher = new ForwardingMessagePublisher(mockPublisher);
    publisher.send(message, "foobar", "routing");
    verify(mockPublisher).send(message, "foobar", "routing");
  }

}
