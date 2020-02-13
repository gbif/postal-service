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
package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.util.UUID;

import org.junit.Test;

public class DeleteDatasetOccurrencesMessageTest {

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(DeleteDatasetOccurrencesMessage.class);
  }

  @Test
  public void testSerDe() throws IOException {
    DeleteDatasetOccurrencesMessage message =
        new DeleteDatasetOccurrencesMessage(
            UUID.randomUUID(), OccurrenceDeletionReason.DATASET_MANUAL);
    Util.testSerDe(message, DeleteDatasetOccurrencesMessage.class);
  }
}
