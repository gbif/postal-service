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

import org.gbif.api.vocabulary.DatasetType;
import org.gbif.api.vocabulary.EndpointType;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * The orchestrator's step header travels on the legacy pipeline messages as an untyped map. It
 * must survive JSON and {@code toBuilder}, and messages without one must serialize as before.
 */
class StepHeaderPassThroughTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final UUID DATASET = UUID.fromString("673f2308-3501-474f-85d5-a832825bb46c");
  private static final Map<String, Object> HEADER =
      Map.of(
          "workflowId", "0b6c1b5e-6a4c-4d7e-9a43-6a0f4c1f7d10",
          "datasetKey", DATASET.toString(),
          "attempt", 2,
          "stepType", "VALIDATOR_DWCDP_STAGE",
          "stepAttempt", 1);

  @Test
  void dwcDpStageMessageCarriesHeader() throws Exception {
    DwcDpStageMessage message =
        new DwcDpStageMessage(DATASET, 2, Set.of("VALIDATOR_DWCDP_STAGE"), null, false, false);
    message.setStepHeader(HEADER);

    DwcDpStageMessage read = MAPPER.readValue(message.toString(), DwcDpStageMessage.class);

    assertEquals(HEADER, read.getStepHeader());
    assertEquals(message, read);
  }

  @Test
  void dwcDpToVerbatimMessageCarriesHeader() throws Exception {
    DwcDpToVerbatimMessage message =
        new DwcDpToVerbatimMessage(
            DATASET, 2, Set.of("VALIDATOR_DWCDP_TO_VERBATIM"), null, false, false, true);
    message.setStepHeader(HEADER);

    DwcDpToVerbatimMessage read =
        MAPPER.readValue(message.toString(), DwcDpToVerbatimMessage.class);

    assertEquals(HEADER, read.getStepHeader());
  }

  @Test
  void verbatimMessageCarriesHeaderThroughJsonAndBuilder() throws Exception {
    PipelinesVerbatimMessage message =
        PipelinesVerbatimMessage.builder()
            .datasetUuid(DATASET)
            .attempt(2)
            .interpretTypes(Set.of("ALL"))
            .pipelineSteps(Set.of("VALIDATOR_VERBATIM_TO_IDENTIFIER"))
            .datasetType(DatasetType.OCCURRENCE)
            .stepHeader(HEADER)
            .build();

    PipelinesVerbatimMessage read =
        MAPPER.readValue(message.toString(), PipelinesVerbatimMessage.class);
    PipelinesVerbatimMessage rebuilt = read.toBuilder().runner("STANDALONE").build();

    assertEquals(HEADER, read.getStepHeader());
    assertEquals(HEADER, rebuilt.getStepHeader());
  }

  @Test
  void interpretedMessageCarriesHeader() throws Exception {
    PipelinesInterpretedMessage message =
        new PipelinesInterpretedMessage(
            DATASET,
            2,
            Set.of("VALIDATOR_COLLECT_METRICS"),
            10L,
            0L,
            null,
            false,
            null,
            null,
            null,
            null,
            Set.of("ALL"),
            DatasetType.OCCURRENCE);
    message.setStepHeader(HEADER);

    PipelinesInterpretedMessage read =
        MAPPER.readValue(message.toString(), PipelinesInterpretedMessage.class);

    assertEquals(HEADER, read.getStepHeader());
    assertEquals(message, read);
  }

  @Test
  void archiveValidatorMessageCarriesHeader() throws Exception {
    PipelinesArchiveValidatorMessage message =
        new PipelinesArchiveValidatorMessage(
            DATASET, 1, Set.of("VALIDATOR_VALIDATE_ARCHIVE"), null, "DWCA");
    message.setStepHeader(HEADER);

    PipelinesArchiveValidatorMessage read =
        MAPPER.readValue(message.toString(), PipelinesArchiveValidatorMessage.class);

    assertEquals(HEADER, read.getStepHeader());
    assertEquals(message, read);
  }

  @Test
  void dwcaMessageCarriesHeader() throws Exception {
    PipelinesDwcaMessage message =
        new PipelinesDwcaMessage(
            DATASET,
            DatasetType.OCCURRENCE,
            URI.create("urn:gbif:validation:" + DATASET),
            1,
            null,
            Set.of("VALIDATOR_DWCA_TO_VERBATIM"),
            EndpointType.DWC_ARCHIVE,
            null,
            null);
    message.setStepHeader(HEADER);

    PipelinesDwcaMessage read = MAPPER.readValue(message.toString(), PipelinesDwcaMessage.class);

    assertEquals(HEADER, read.getStepHeader());
    assertEquals(message, read);
    assertEquals(PipelinesDwcaMessage.VALIDATOR_ROUTING_KEY, read.getRoutingKey());
  }

  @Test
  void checklistValidatorMessageCarriesHeader() throws Exception {
    PipelinesChecklistValidatorMessage message =
        new PipelinesChecklistValidatorMessage(DATASET, "{\"status\":\"finished\"}")
            .setStepHeader(HEADER);

    PipelinesChecklistValidatorMessage read =
        MAPPER.readValue(
            MAPPER.writeValueAsString(message), PipelinesChecklistValidatorMessage.class);

    assertEquals(HEADER, read.getStepHeader());
    assertEquals("{\"status\":\"finished\"}", read.getClBResponsePayload());
  }

  @Test
  void messagesWithoutHeaderSerializeAsBefore() throws Exception {
    DwcDpStageMessage stage =
        new DwcDpStageMessage(DATASET, 2, Set.of("VALIDATOR_DWCDP_STAGE"), null, true, false);
    PipelinesVerbatimMessage verbatim =
        PipelinesVerbatimMessage.builder().datasetUuid(DATASET).interpretTypes(Set.of()).build();

    assertFalse(stage.toString().contains("stepHeader"));
    assertFalse(verbatim.toString().contains("stepHeader"));
    assertFalse(
        MAPPER
            .writeValueAsString(new PipelinesChecklistValidatorMessage(DATASET, "{}"))
            .contains("stepHeader"));
    assertFalse(
        new PipelinesArchiveValidatorMessage(DATASET, 1, Set.of(), null, "DWCA")
            .toString()
            .contains("stepHeader"));
    assertNull(MAPPER.readValue(stage.toString(), DwcDpStageMessage.class).getStepHeader());
  }
}
