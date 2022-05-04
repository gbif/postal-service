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

import org.gbif.api.vocabulary.EndpointType;
import org.gbif.common.messaging.api.messages.PipelinesVerbatimMessage.ValidationResult;
import org.gbif.utils.PreconditionUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This message indicates that the events of a dataset have been indexed.
 */
public class PipelinesEventsIndexedMessage implements PipelineBasedMessage {

  public static final String ROUTING_KEY = "occurrence.pipelines.events.indexed";

  private UUID datasetUuid;
  private int attempt;
  private Set<String> pipelineSteps;
  // TODO: remove numberOfRecords??
  private Long numberOfRecords;
  private String resetPrefix;
  private Long executionId;
  private EndpointType endpointType;
  private ValidationResult validationResult;
  private boolean isValidator = false;

  public PipelinesEventsIndexedMessage() {}

  @JsonCreator
  public PipelinesEventsIndexedMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("attempt") int attempt,
      @JsonProperty("pipelineSteps") Set<String> pipelineSteps,
      @JsonProperty("numberOfRecords") Long numberOfRecords,
      @JsonProperty("resetPrefix") String resetPrefix,
      @JsonProperty("executionId") Long executionId,
      @JsonProperty("endpointType") EndpointType endpointType,
      @JsonProperty("validationResult") ValidationResult validationResult,
      @JsonProperty("isValidator") Boolean isValidator) {
    this.datasetUuid = Objects.requireNonNull(datasetUuid, "datasetUuid can't be null");
    PreconditionUtils.checkArgument(attempt >= 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    this.pipelineSteps = pipelineSteps == null ? Collections.emptySet() : pipelineSteps;
    this.numberOfRecords = numberOfRecords;
    this.resetPrefix = resetPrefix;
    this.executionId = executionId;
    this.endpointType = endpointType;
    this.validationResult = validationResult;
    if (isValidator != null) {
      this.isValidator = isValidator;
    }
  }

  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  @Override
  public Integer getAttempt() {
    return attempt;
  }

  @Override
  public Set<String> getPipelineSteps() {
    return pipelineSteps;
  }

  @Override
  public Long getExecutionId() {
    return executionId;
  }

  @Override
  public String getRoutingKey() {
    String key = ROUTING_KEY;
    if (isValidator) {
      key = key + "." + "validator";
    }
    return key;
  }

  public Long getNumberOfRecords() {
    return numberOfRecords;
  }

  public String getResetPrefix() {
    return resetPrefix;
  }

  public EndpointType getEndpointType() {
    return endpointType;
  }

  public ValidationResult getValidationResult() {
    return validationResult;
  }

  public boolean isValidator() {
    return isValidator;
  }

  public PipelinesEventsIndexedMessage setDatasetUuid(UUID datasetUuid) {
    this.datasetUuid = datasetUuid;
    return this;
  }

  public PipelinesEventsIndexedMessage setAttempt(int attempt) {
    this.attempt = attempt;
    return this;
  }

  public PipelinesEventsIndexedMessage setPipelineSteps(Set<String> pipelineSteps) {
    this.pipelineSteps = pipelineSteps;
    return this;
  }

  public PipelinesEventsIndexedMessage setNumberOfRecords(Long numberOfRecords) {
    this.numberOfRecords = numberOfRecords;
    return this;
  }

  public PipelinesEventsIndexedMessage setResetPrefix(String resetPrefix) {
    this.resetPrefix = resetPrefix;
    return this;
  }

  public PipelinesEventsIndexedMessage setEndpointType(EndpointType endpointType) {
    this.endpointType = endpointType;
    return this;
  }

  public PipelinesEventsIndexedMessage setValidationResult(ValidationResult validationResult) {
    this.validationResult = validationResult;
    return this;
  }

  public PipelinesEventsIndexedMessage setValidator(boolean validator) {
    isValidator = validator;
    return this;
  }

  @Override
  public void setExecutionId(Long executionId) {
    this.executionId = executionId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PipelinesEventsIndexedMessage that = (PipelinesEventsIndexedMessage) o;
    return attempt == that.attempt
        && Objects.equals(datasetUuid, that.datasetUuid)
        && Objects.equals(pipelineSteps, that.pipelineSteps)
        && Objects.equals(resetPrefix, that.resetPrefix)
        && Objects.equals(executionId, that.executionId)
        && Objects.equals(endpointType, that.endpointType)
        && Objects.equals(numberOfRecords, that.numberOfRecords)
        && Objects.equals(validationResult, that.validationResult)
        && isValidator == that.isValidator;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        datasetUuid,
        attempt,
        pipelineSteps,
        resetPrefix,
        executionId,
        numberOfRecords,
        endpointType,
        validationResult,
        isValidator);
  }

  @Override
  public String toString() {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.writeValueAsString(this);
    } catch (IOException e) {
      // NOP
    }
    return "";
  }
}
