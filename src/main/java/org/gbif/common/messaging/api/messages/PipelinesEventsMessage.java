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
 * This message is used to start the processing of events datasets.
 */
public class PipelinesEventsMessage implements PipelineBasedMessage {

  public static final String ROUTING_KEY = "occurrence.pipelines.events";

  private UUID datasetUuid;
  private int attempt;
  private Set<String> pipelineSteps;
  private String runner;
  private Long numberOfOccurrenceRecords;
  private Long numberOfEventRecords;
  private boolean repeatAttempt;
  private String resetPrefix;
  private String onlyForStep;
  private Long executionId;
  private EndpointType endpointType;
  private ValidationResult validationResult;
  private Set<String> interpretTypes;
  private DatasetType datasetType;

  public PipelinesEventsMessage() {}

  @JsonCreator
  public PipelinesEventsMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("attempt") int attempt,
      @JsonProperty("pipelineSteps") Set<String> pipelineSteps,
      @JsonProperty("numberOfEventRecords") Long numberOfEventRecords,
      @JsonProperty("numberOfOccurrenceRecords") Long numberOfOccurrenceRecords,
      @JsonProperty("runner") String runner,
      @JsonProperty("repeatAttempt") boolean repeatAttempt,
      @JsonProperty("resetPrefix") String resetPrefix,
      @JsonProperty("onlyForStep") String onlyForStep,
      @JsonProperty("executionId") Long executionId,
      @JsonProperty("endpointType") EndpointType endpointType,
      @JsonProperty("validationResult") ValidationResult validationResult,
      @JsonProperty("interpretTypes") Set<String> interpretTypes,
      @JsonProperty("datasetType") DatasetType datasetType) {
    this.datasetUuid = Objects.requireNonNull(datasetUuid, "datasetUuid can't be null");
    PreconditionUtils.checkArgument(attempt >= 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    this.pipelineSteps = pipelineSteps == null ? Collections.emptySet() : pipelineSteps;
    this.runner = runner;
    this.numberOfEventRecords = numberOfEventRecords;
    this.numberOfOccurrenceRecords = numberOfOccurrenceRecords;
    this.repeatAttempt = repeatAttempt;
    this.resetPrefix = resetPrefix;
    this.onlyForStep = onlyForStep;
    this.executionId = executionId;
    this.endpointType = endpointType;
    this.validationResult = validationResult;
    this.interpretTypes = interpretTypes == null ? Collections.emptySet() : interpretTypes;
    this.datasetType = datasetType;
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
    if (runner != null && !runner.isEmpty()) {
      key = key + "." + runner.toLowerCase();
    }
    return key;
  }

  public Long getNumberOfOccurrenceRecords() {
    return numberOfOccurrenceRecords;
  }

  public Long getNumberOfEventRecords() {
    return numberOfEventRecords;
  }

  public String getRunner() {
    return runner;
  }

  public boolean isRepeatAttempt() {
    return repeatAttempt;
  }

  public String getResetPrefix() {
    return resetPrefix;
  }

  public String getOnlyForStep() {
    return onlyForStep;
  }

  public EndpointType getEndpointType() {
    return endpointType;
  }

  public ValidationResult getValidationResult() {
    return validationResult;
  }

  public Set<String> getInterpretTypes() {
    return interpretTypes;
  }

  public DatasetType getDatasetType() {
    return datasetType;
  }

  public PipelinesEventsMessage setNumberOfOccurrenceRecords(Long numberOfOccurrenceRecords) {
    this.numberOfOccurrenceRecords = numberOfOccurrenceRecords;
    return this;
  }

  public PipelinesEventsMessage setNumberOfEventRecords(Long numberOfEventRecords) {
    this.numberOfEventRecords = numberOfEventRecords;
    return this;
  }

  public PipelinesEventsMessage setDatasetUuid(UUID datasetUuid) {
    this.datasetUuid = datasetUuid;
    return this;
  }

  public PipelinesEventsMessage setAttempt(int attempt) {
    this.attempt = attempt;
    return this;
  }

  public PipelinesEventsMessage setPipelineSteps(Set<String> pipelineSteps) {
    this.pipelineSteps = pipelineSteps;
    return this;
  }

  public PipelinesEventsMessage setRunner(String runner) {
    this.runner = runner;
    return this;
  }

  public PipelinesEventsMessage setRepeatAttempt(boolean repeatAttempt) {
    this.repeatAttempt = repeatAttempt;
    return this;
  }

  public PipelinesEventsMessage setResetPrefix(String resetPrefix) {
    this.resetPrefix = resetPrefix;
    return this;
  }

  public PipelinesEventsMessage setOnlyForStep(String onlyForStep) {
    this.onlyForStep = onlyForStep;
    return this;
  }

  public PipelinesEventsMessage setEndpointType(EndpointType endpointType) {
    this.endpointType = endpointType;
    return this;
  }

  public PipelinesEventsMessage setValidationResult(ValidationResult validationResult) {
    this.validationResult = validationResult;
    return this;
  }

  public PipelinesEventsMessage setInterpretTypes(Set<String> interpretTypes) {
    this.interpretTypes = interpretTypes;
    return this;
  }

  public PipelinesEventsMessage setDatasetType(DatasetType datasetType) {
    this.datasetType = datasetType;
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
    PipelinesEventsMessage that = (PipelinesEventsMessage) o;
    return attempt == that.attempt
        && repeatAttempt == that.repeatAttempt
        && Objects.equals(datasetUuid, that.datasetUuid)
        && Objects.equals(pipelineSteps, that.pipelineSteps)
        && Objects.equals(runner, that.runner)
        && Objects.equals(resetPrefix, that.resetPrefix)
        && Objects.equals(onlyForStep, that.onlyForStep)
        && Objects.equals(executionId, that.executionId)
        && Objects.equals(endpointType, that.endpointType)
        && Objects.equals(numberOfEventRecords, that.numberOfEventRecords)
        && Objects.equals(numberOfOccurrenceRecords, that.numberOfOccurrenceRecords)
        && Objects.equals(validationResult, that.validationResult)
        && Objects.equals(interpretTypes, that.interpretTypes)
        && datasetType == that.datasetType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        datasetUuid,
        attempt,
        pipelineSteps,
        runner,
        repeatAttempt,
        resetPrefix,
        onlyForStep,
        executionId,
        numberOfEventRecords,
        numberOfOccurrenceRecords,
        endpointType,
        validationResult,
        interpretTypes,
        datasetType);
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
