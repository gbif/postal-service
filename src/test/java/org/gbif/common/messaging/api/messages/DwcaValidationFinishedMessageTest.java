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

import org.gbif.api.vocabulary.DatasetType;
import org.gbif.api.vocabulary.EndpointType;
import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import org.junit.jupiter.api.Test;

public class DwcaValidationFinishedMessageTest {

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(DwcaValidationFinishedMessage.class);
  }

  @Test
  public void testSerDe() throws IOException, URISyntaxException {
    UUID uuid = UUID.randomUUID();
    DwcaValidationFinishedMessage message =
        new DwcaValidationFinishedMessage(
            uuid,
            DatasetType.OCCURRENCE,
            new URI("http://google.de"),
            1,
            OccurrenceFragmentedMessageTest.report(uuid),
            EndpointType.DWC_ARCHIVE,
            Platform.ALL);
    Util.testSerDe(message, DwcaValidationFinishedMessage.class);
  }
}
