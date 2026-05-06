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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gbif.api.vocabulary.DatasetType;
import org.gbif.api.vocabulary.EndpointType;
import org.gbif.common.messaging.api.validation.ValidationResult;
import org.gbif.utils.PreconditionUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractPipelinesInterpretedMessage
    implements PipelinesInterpretationMessage, PipelinesRunnerMessage {

  protected UUID datasetUuid;
  protected int attempt;
  protected Set<String> pipelineSteps;
  protected String runner;
  protected Long numberOfRecords;
  protected Long numberOfEventRecords;
  protected boolean repeatAttempt;
  protected String resetPrefix;
  protected Long executionId;
  protected EndpointType endpointType;
  protected ValidationResult validationResult;
  protected Set<String> interpretTypes;
  protected DatasetType datasetType;

  public AbstractPipelinesInterpretedMessage() {}

  @JsonCreator
  public AbstractPipelinesInterpretedMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("attempt") int attempt,
      @JsonProperty("pipelineSteps") Set<String> pipelineSteps,
      @JsonProperty("numberOfRecords") Long numberOfRecords,
      @JsonProperty("numberOfEventRecords") Long numberOfEventRecords,
      @JsonProperty("runner") String runner,
      @JsonProperty("repeatAttempt") boolean repeatAttempt,
      @JsonProperty("resetPrefix") String resetPrefix,
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
    this.numberOfRecords = numberOfRecords;
    this.numberOfEventRecords = numberOfEventRecords;
    this.repeatAttempt = repeatAttempt;
    this.resetPrefix = resetPrefix;
    this.executionId = executionId;
    this.endpointType = endpointType;
    this.validationResult = validationResult;
    this.interpretTypes = interpretTypes == null ? Collections.emptySet() : interpretTypes;
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
  public abstract String getRoutingKey();

  public Long getNumberOfRecords() {
    return numberOfRecords;
  }

  public Long getNumberOfEventRecords() {
    return numberOfEventRecords;
  }

  @Override
  public String getRunner() {
    return runner;
  }

  public boolean isRepeatAttempt() {
    return repeatAttempt;
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

  @Override
  public Set<String> getInterpretTypes() {
    return interpretTypes;
  }

  public AbstractPipelinesInterpretedMessage setDatasetUuid(UUID datasetUuid) {
    this.datasetUuid = datasetUuid;
    return this;
  }

  public AbstractPipelinesInterpretedMessage setAttempt(int attempt) {
    this.attempt = attempt;
    return this;
  }

  public AbstractPipelinesInterpretedMessage setPipelineSteps(Set<String> pipelineSteps) {
    this.pipelineSteps = pipelineSteps;
    return this;
  }

  public AbstractPipelinesInterpretedMessage setNumberOfRecords(Long numberOfRecords) {
    this.numberOfRecords = numberOfRecords;
    return this;
  }

  public AbstractPipelinesInterpretedMessage setNumberOfEventRecords(Long numberOfEventRecords) {
    this.numberOfEventRecords = numberOfEventRecords;
    return this;
  }

  public AbstractPipelinesInterpretedMessage setRunner(String runner) {
    this.runner = runner;
    return this;
  }

  public AbstractPipelinesInterpretedMessage setRepeatAttempt(boolean repeatAttempt) {
    this.repeatAttempt = repeatAttempt;
    return this;
  }

  public AbstractPipelinesInterpretedMessage setResetPrefix(String resetPrefix) {
    this.resetPrefix = resetPrefix;
    return this;
  }

  public AbstractPipelinesInterpretedMessage setEndpointType(EndpointType endpointType) {
    this.endpointType = endpointType;
    return this;
  }

  public AbstractPipelinesInterpretedMessage setValidationResult(ValidationResult validationResult) {
    this.validationResult = validationResult;
    return this;
  }

  @Override
  public void setInterpretTypes(Set<String> interpretTypes) {
    this.interpretTypes = interpretTypes;
  }

  public DatasetType getDatasetType() {
    return datasetType;
  }

  public AbstractPipelinesInterpretedMessage setDatasetType(DatasetType datasetType) {
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
    AbstractPipelinesInterpretedMessage that = (AbstractPipelinesInterpretedMessage) o;
    return attempt == that.attempt
        && repeatAttempt == that.repeatAttempt
        && Objects.equals(datasetUuid, that.datasetUuid)
        && Objects.equals(pipelineSteps, that.pipelineSteps)
        && Objects.equals(runner, that.runner)
        && Objects.equals(resetPrefix, that.resetPrefix)
        && Objects.equals(executionId, that.executionId)
        && Objects.equals(endpointType, that.endpointType)
        && Objects.equals(numberOfRecords, that.numberOfRecords)
        && Objects.equals(numberOfEventRecords, that.numberOfEventRecords)
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
        executionId,
        numberOfRecords,
        numberOfEventRecords,
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
