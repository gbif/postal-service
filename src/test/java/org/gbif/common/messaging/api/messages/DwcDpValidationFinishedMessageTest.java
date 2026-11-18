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

import org.gbif.common.messaging.util.MessageUtils;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/** The analyser's message (validationReport = the whole AnalysisExecution) as postal reads it. */
class DwcDpValidationFinishedMessageTest {

  private static final String JSON =
      "{\"datasetUuid\":\"673f2308-3501-474f-85d5-a832825bb46c\",\"attempt\":2,\"valid\":false,"
          + "\"validationReport\":{"
          + "\"result\":{\"descriptorValidation\":null,\"emlValidation\":null,"
          + "\"resourceAnalysisResults\":[]},"
          + "\"metadata\":{\"started\":\"2026-11-18T10:00:00.25\","
          + "\"finished\":\"2026-11-18T10:00:04.5\",\"features\":[],\"isValid\":false},"
          + "\"version\":\"1.0\"}}";

  @Test
  void readsWithPostalListenerSettings() throws Exception {
    // What MessageListener does to its mapper.
    ObjectMapper mapper =
        MessageUtils.configure(
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false));

    DwcDpValidationFinishedMessage message =
        mapper.readValue(JSON, DwcDpValidationFinishedMessage.class);

    assertEquals(UUID.fromString("673f2308-3501-474f-85d5-a832825bb46c"), message.getDatasetUuid());
    assertFalse(message.isValid());
    assertNotNull(message.getValidationReport().result());
    assertFalse(message.getValidationReport().metadata().isValid());
    assertEquals(
        LocalDateTime.of(2026, 11, 18, 10, 0, 0, 250_000_000),
        message.getValidationReport().metadata().started());
  }
}
