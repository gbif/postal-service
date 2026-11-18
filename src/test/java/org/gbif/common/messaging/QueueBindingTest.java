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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QueueBindingTest {

  @Test
  void withoutDeadLetter() {
    QueueBinding binding = QueueBinding.builder("q", "x", "rk").build();

    assertEquals(1, binding.getThreads());
    assertNull(binding.getDeadLetterExchange());
    assertNull(binding.getDeadLetterQueue());
    assertTrue(binding.getArguments().isEmpty());
  }

  @Test
  void deadLetterAddsArguments() {
    QueueBinding binding = QueueBinding.builder("q", "x", "rk").deadLetter("dlx").build();

    assertEquals("q.dlq", binding.getDeadLetterQueue());
    assertEquals("dlx", binding.getArguments().get("x-dead-letter-exchange"));
    assertEquals("q", binding.getArguments().get("x-dead-letter-routing-key"));
  }

  @Test
  void threadsMustBePositive() {
    assertThrows(
        IllegalArgumentException.class, () -> QueueBinding.builder("q", "x", "rk").threads(0));
  }
}
