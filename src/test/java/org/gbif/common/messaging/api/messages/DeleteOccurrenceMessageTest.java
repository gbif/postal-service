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
package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Util;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class DeleteOccurrenceMessageTest {

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(DeleteOccurrenceMessage.class);
  }

  @Test
  public void testSerDe() throws IOException {
    DeleteOccurrenceMessage message =
        new DeleteOccurrenceMessage(1, OccurrenceDeletionReason.DATASET_MANUAL, null, null);
    Util.testSerDe(message, DeleteOccurrenceMessage.class);

    message = new DeleteOccurrenceMessage(1, OccurrenceDeletionReason.NOT_SEEN_IN_LAST_CRAWL, 1, 2);
    Util.testSerDe(message, DeleteOccurrenceMessage.class);
  }
}
