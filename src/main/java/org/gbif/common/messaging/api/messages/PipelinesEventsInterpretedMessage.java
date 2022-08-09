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
import org.gbif.utils.PreconditionUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/** This message indicates that the events of a dataset have been interpreted. */
public class PipelinesEventsInterpretedMessage implements PipelinesInterpretationMessage {

  public static final String ROUTING_KEY = "occurrence.pipelines.events.interpreted";

  private UUID datasetUuid;
  private int attempt;
  private Set<String> pipelineSteps;
  private Long numberOfOccurrenceRecords;
  private Long numberOfEventRecords;
  private String resetPrefix;
  private String onlyForStep;
  private Long executionId;
  private EndpointType endpointType;
  private Set<String> interpretTypes;
  private boolean repeatAttempt;
  private String runner;

  public PipelinesEventsInterpretedMessage() {}

  @JsonCreator
  public PipelinesEventsInterpretedMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("attempt") int attempt,
      @JsonProperty("pipelineSteps") Set<String> pipelineSteps,
      @JsonProperty("numberOfOccurrenceRecords") Long numberOfOccurrenceRecords,
      @JsonProperty("numberOfEventRecords") Long numberOfEventRecords,
      @JsonProperty("resetPrefix") String resetPrefix,
      @JsonProperty("onlyForStep") String onlyForStep,
      @JsonProperty("executionId") Long executionId,
      @JsonProperty("endpointType") EndpointType endpointType,
      @JsonProperty("interpretTypes") Set<String> interpretTypes,
      @JsonProperty("repeatAttempt") boolean repeatAttempt,
      @JsonProperty("runner") String runner) {
    this.datasetUuid = Objects.requireNonNull(datasetUuid, "datasetUuid can't be null");
    PreconditionUtils.checkArgument(attempt >= 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    this.pipelineSteps = pipelineSteps == null ? Collections.emptySet() : pipelineSteps;
    this.numberOfOccurrenceRecords = numberOfOccurrenceRecords;
    this.numberOfEventRecords = numberOfEventRecords;
    this.resetPrefix = resetPrefix;
    this.onlyForStep = onlyForStep;
    this.executionId = executionId;
    this.endpointType = endpointType;
    this.interpretTypes = interpretTypes == null ? Collections.emptySet() : interpretTypes;
    this.repeatAttempt = repeatAttempt;
    this.runner = runner;
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

  @JsonIgnore
  @Override
  public Long getNumberOfInterpretationRecords() {
    return Optional.ofNullable(getNumberOfEventRecords()).orElse(0L)
        + Optional.ofNullable(getNumberOfOccurrenceRecords()).orElse(0L);
  }

  public String getResetPrefix() {
    return resetPrefix;
  }

  public EndpointType getEndpointType() {
    return endpointType;
  }

  @Override
  public Set<String> getInterpretTypes() {
    return interpretTypes;
  }

  @Override
  public String getOnlyForStep() {
    return onlyForStep;
  }

  public boolean isRepeatAttempt() {
    return repeatAttempt;
  }

  @Override
  public String getRunner() {
    return runner;
  }

  public PipelinesEventsInterpretedMessage setDatasetUuid(UUID datasetUuid) {
    this.datasetUuid = datasetUuid;
    return this;
  }

  public PipelinesEventsInterpretedMessage setAttempt(int attempt) {
    this.attempt = attempt;
    return this;
  }

  public PipelinesEventsInterpretedMessage setPipelineSteps(Set<String> pipelineSteps) {
    this.pipelineSteps = pipelineSteps;
    return this;
  }

  public PipelinesEventsInterpretedMessage setNumberOfOccurrenceRecords(
      Long numberOfOccurrenceRecords) {
    this.numberOfOccurrenceRecords = numberOfOccurrenceRecords;
    return this;
  }

  public PipelinesEventsInterpretedMessage setNumberOfEventRecords(Long numberOfEventRecords) {
    this.numberOfEventRecords = numberOfEventRecords;
    return this;
  }

  public PipelinesEventsInterpretedMessage setResetPrefix(String resetPrefix) {
    this.resetPrefix = resetPrefix;
    return this;
  }

  public PipelinesEventsInterpretedMessage setOnlyForStep(String onlyForStep) {
    this.onlyForStep = onlyForStep;
    return this;
  }

  public PipelinesEventsInterpretedMessage setEndpointType(EndpointType endpointType) {
    this.endpointType = endpointType;
    return this;
  }

  @Override
  public void setInterpretTypes(Set<String> interpretTypes) {
    this.interpretTypes = interpretTypes;
  }

  public PipelinesEventsInterpretedMessage setRepeatAttempt(boolean repeatAttempt) {
    this.repeatAttempt = repeatAttempt;
    return this;
  }

  public PipelinesEventsInterpretedMessage setRunner(String runner) {
    this.runner = runner;
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
    PipelinesEventsInterpretedMessage that = (PipelinesEventsInterpretedMessage) o;
    return attempt == that.attempt
        && Objects.equals(datasetUuid, that.datasetUuid)
        && Objects.equals(pipelineSteps, that.pipelineSteps)
        && Objects.equals(resetPrefix, that.resetPrefix)
        && Objects.equals(onlyForStep, that.onlyForStep)
        && Objects.equals(executionId, that.executionId)
        && Objects.equals(endpointType, that.endpointType)
        && Objects.equals(numberOfOccurrenceRecords, that.numberOfOccurrenceRecords)
        && Objects.equals(numberOfEventRecords, that.numberOfEventRecords)
        && Objects.equals(interpretTypes, that.interpretTypes)
        && repeatAttempt == that.repeatAttempt
        && Objects.equals(runner, that.runner);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        datasetUuid,
        attempt,
        pipelineSteps,
        resetPrefix,
        onlyForStep,
        executionId,
        numberOfOccurrenceRecords,
        numberOfEventRecords,
        endpointType,
        interpretTypes,
        repeatAttempt,
        runner);
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
