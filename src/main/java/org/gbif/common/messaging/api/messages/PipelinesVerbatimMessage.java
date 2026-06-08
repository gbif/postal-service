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
import org.gbif.common.messaging.ExchangeType;
import org.gbif.common.messaging.MessageBinding;
import org.gbif.common.messaging.util.MessageUtils;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static org.gbif.api.model.pipelines.StepType.VALIDATOR_VERBATIM_TO_INTERPRETED;
import static org.gbif.api.model.pipelines.StepType.VERBATIM_TO_IDENTIFIER;

/**
 * Message is published when the conversion from of dataset from various formats(DwC or Xml) to
 * avro(ExtendedRecord) is done.
 */
@Getter
@EqualsAndHashCode
@MessageBinding(exchange = ExchangeType.OCCURRENCE, routingKey = PipelinesVerbatimMessage.ROUTING_KEY)
public class PipelinesVerbatimMessage implements PipelineBasedMessage, PipelinesRunnerMessage {

  public static final String ROUTING_KEY = "occurrence.pipelines.verbatim.finished";

  private UUID datasetUuid;
  private Integer attempt;
  /**
   * @return types of interpretation - ALL, LOCATION, BASE or etc.
   */
  @Setter
  private Set<String> interpretTypes;
  @Setter
  private Set<String> pipelineSteps;
  private String runner;
  private EndpointType endpointType;
  private String extraPath;
  private ValidationResult validationResult;
  @Setter
  private String resetPrefix;
  @Setter
  private Long executionId;
  private DatasetType datasetType;

  public PipelinesVerbatimMessage() {}

  @JsonCreator
  public PipelinesVerbatimMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("attempt") Integer attempt,
      @JsonProperty("interpretTypes") Set<String> interpretTypes,
      @JsonProperty("pipelineSteps") Set<String> pipelineSteps,
      @JsonProperty("runner") String runner,
      @JsonProperty("endpointType") EndpointType endpointType,
      @JsonProperty("extraPath") String extraPath,
      @JsonProperty("validationResult") ValidationResult validationResult,
      @JsonProperty("resetPrefix") String resetPrefix,
      @JsonProperty("executionId") Long executionId,
      @JsonProperty("datasetType") DatasetType datasetType) {
    this.datasetUuid = Objects.requireNonNull(datasetUuid, "datasetUuid can't be null");
    this.interpretTypes = Objects.requireNonNull(interpretTypes, "interpretTypes can't be null");
    this.attempt = attempt;
    this.pipelineSteps = pipelineSteps == null ? Collections.emptySet() : pipelineSteps;
    this.runner = runner;
    this.endpointType = endpointType;
    this.extraPath = extraPath;
    this.validationResult = validationResult;
    this.resetPrefix = resetPrefix;
    this.executionId = executionId;
    this.datasetType = datasetType;
  }

  @Override
  public DatasetInfo getDatasetInfo() {
    boolean containsOccurrences =
        Optional.ofNullable(validationResult)
            .map(ValidationResult::getNumberOfRecords)
            .map(count -> count > 0)
            .orElse(false);
    boolean containsEvents =
        Optional.ofNullable(validationResult)
            .map(ValidationResult::getNumberOfEventRecords)
            .map(count -> count > 0)
            .orElse(false);
    return new DatasetInfo(datasetType, containsOccurrences, containsEvents);
  }

  @Override
  public String getRoutingKey() {
    StringJoiner key = new StringJoiner(".").add(ROUTING_KEY);

    if (pipelineSteps.contains(VALIDATOR_VERBATIM_TO_INTERPRETED.name())) {
      key.add("validator");
    }
    if (pipelineSteps.contains(VERBATIM_TO_IDENTIFIER.name())) {
      key.add("identifier");
    }
    if (runner != null && !runner.isEmpty()) {
      key.add(runner.toLowerCase());
    }

    return key.toString();
  }

  @Override
  public String toString() {
    return MessageUtils.toString(this);
  }

  @Setter
  @Getter
  @EqualsAndHashCode
  public static class ValidationResult {

    private boolean tripletValid;
    private boolean occurrenceIdValid;
    private Boolean useExtendedRecordId;
    private Long numberOfRecords;
    private Long numberOfEventRecords;

    public ValidationResult() {}

    @JsonCreator
    public ValidationResult(
        @JsonProperty("tripletValid") boolean tripletValid,
        @JsonProperty("occurrenceIdValid") boolean occurrenceIdValid,
        @JsonProperty("useExtendedRecordId") Boolean useExtendedRecordId,
        @JsonProperty("numberOfRecords") Long numberOfRecords,
        @JsonProperty("numberOfEventRecords") Long numberOfEventRecords) {
      this.tripletValid = tripletValid;
      this.occurrenceIdValid = occurrenceIdValid;
      this.useExtendedRecordId = useExtendedRecordId;
      this.numberOfRecords = numberOfRecords;
      this.numberOfEventRecords = numberOfEventRecords;
    }

    @Override
    public String toString() {
      return MessageUtils.toString(this);
    }

  }
}
