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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ForwardingMessagePublisherTest {

  private final TestMessage message = new TestMessage();

  @Mock private MessagePublisher mockPublisher;

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
