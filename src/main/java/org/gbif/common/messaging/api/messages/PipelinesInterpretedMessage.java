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

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This message instructs the dataset mutator service to send InterpretDatasetMessage for each
 * occurrence in the dataset.
 */
public class PipelinesInterpretedMessage implements PipelineBasedMessage {

  public static final String ROUTING_KEY = "occurrence.pipelines.interpretation.finished";

  private UUID datasetUuid;
  private int attempt;
  private Set<String> pipelineSteps;
  private String runner;
  private Long numberOfRecords;
  private boolean repeatAttempt;
  private String resetPrefix;
  private String onlyForStep;
  private Long executionId;

  public PipelinesInterpretedMessage() {}

  @JsonCreator
  public PipelinesInterpretedMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("attempt") int attempt,
      @JsonProperty("pipelineSteps") Set<String> pipelineSteps,
      @JsonProperty("numberOfRecords") Long numberOfRecords,
      @JsonProperty("runner") String runner,
      @JsonProperty("repeatAttempt") boolean repeatAttempt,
      @JsonProperty("resetPrefix") String resetPrefix,
      @JsonProperty("onlyForStep") String onlyForStep,
      @JsonProperty("executionId") Long executionId) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    checkArgument(attempt >= 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    this.pipelineSteps = pipelineSteps == null ? Collections.emptySet() : pipelineSteps;
    this.runner = runner;
    this.numberOfRecords = numberOfRecords;
    this.repeatAttempt = repeatAttempt;
    this.resetPrefix = resetPrefix;
    this.onlyForStep = onlyForStep;
    this.executionId = executionId;
  }

  public PipelinesInterpretedMessage(
      UUID datasetUuid,
      int attempt,
      Set<String> pipelineSteps,
      Long numberOfRecords,
      boolean repeatAttempt,
      String resetPrefix) {
    this(
        datasetUuid,
        attempt,
        pipelineSteps,
        numberOfRecords,
        null,
        repeatAttempt,
        resetPrefix,
        null,
        null);
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
    return ROUTING_KEY;
  }

  public String getRunner() {
    return runner;
  }

  public Long getNumberOfRecords() {
    return numberOfRecords;
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

  public PipelinesInterpretedMessage setDatasetUuid(UUID datasetUuid) {
    this.datasetUuid = datasetUuid;
    return this;
  }

  public PipelinesInterpretedMessage setAttempt(int attempt) {
    this.attempt = attempt;
    return this;
  }

  public PipelinesInterpretedMessage setPipelineSteps(Set<String> pipelineSteps) {
    this.pipelineSteps = pipelineSteps;
    return this;
  }

  public PipelinesInterpretedMessage setRunner(String runner) {
    this.runner = runner;
    return this;
  }

  public PipelinesInterpretedMessage setNumberOfRecords(Long numberOfRecords) {
    this.numberOfRecords = numberOfRecords;
    return this;
  }

  public PipelinesInterpretedMessage setRepeatAttempt(boolean repeatAttempt) {
    this.repeatAttempt = repeatAttempt;
    return this;
  }

  public PipelinesInterpretedMessage setResetPrefix(String resetPrefix) {
    this.resetPrefix = resetPrefix;
    return this;
  }

  public PipelinesInterpretedMessage setOnlyForStep(String onlyForStep) {
    this.onlyForStep = onlyForStep;
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
    PipelinesInterpretedMessage that = (PipelinesInterpretedMessage) o;
    return attempt == that.attempt
        && repeatAttempt == that.repeatAttempt
        && Objects.equals(datasetUuid, that.datasetUuid)
        && Objects.equals(pipelineSteps, that.pipelineSteps)
        && Objects.equals(runner, that.runner)
        && Objects.equals(numberOfRecords, that.numberOfRecords)
        && Objects.equals(resetPrefix, that.resetPrefix)
        && Objects.equals(onlyForStep, that.onlyForStep)
        && Objects.equals(executionId, that.executionId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        datasetUuid,
        attempt,
        pipelineSteps,
        runner,
        numberOfRecords,
        repeatAttempt,
        resetPrefix,
        onlyForStep,
        executionId);
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
