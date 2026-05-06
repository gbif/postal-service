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

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public abstract class AbstractPipelinesVerbatimMessage implements PipelineBasedMessage, PipelinesRunnerMessage {

  protected UUID datasetUuid;
  protected Integer attempt;
  protected Set<String> interpretTypes;
  protected Set<String> pipelineSteps;
  protected String runner;
  protected EndpointType endpointType;
  protected String extraPath;
  protected ValidationResult validationResult;
  protected String resetPrefix;
  protected Long executionId;
  protected DatasetType datasetType;

  public AbstractPipelinesVerbatimMessage() {}

  @JsonCreator
  public AbstractPipelinesVerbatimMessage(
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
  public PipelineBasedMessage.DatasetInfo getDatasetInfo() {
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
    return new PipelineBasedMessage.DatasetInfo(datasetType, containsOccurrences, containsEvents);
  }

  /** @return datasetUUID for the converted dataset */
  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  /** @return attempt for the converted dataset */
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

  /** @return types of interpretation - ALL, LOCATION, BASE or etc. */
  public Set<String> getInterpretTypes() {
    return interpretTypes;
  }

  @Override
  public abstract String getRoutingKey();

  @Override
  public String getRunner() {
    return runner;
  }

  public EndpointType getEndpointType() {
    return endpointType;
  }

  public String getExtraPath() {
    return extraPath;
  }

  public ValidationResult getValidationResult() {
    return validationResult;
  }

  public String getResetPrefix() {
    return resetPrefix;
  }

  public AbstractPipelinesVerbatimMessage setDatasetUuid(UUID datasetUuid) {
    this.datasetUuid = datasetUuid;
    return this;
  }

  public AbstractPipelinesVerbatimMessage setAttempt(Integer attempt) {
    this.attempt = attempt;
    return this;
  }

  public AbstractPipelinesVerbatimMessage setInterpretTypes(Set<String> interpretTypes) {
    this.interpretTypes = interpretTypes;
    return this;
  }

  public AbstractPipelinesVerbatimMessage setPipelineSteps(Set<String> pipelineSteps) {
    this.pipelineSteps = pipelineSteps;
    return this;
  }

  public AbstractPipelinesVerbatimMessage setRunner(String runner) {
    this.runner = runner;
    return this;
  }

  public AbstractPipelinesVerbatimMessage setEndpointType(EndpointType endpointType) {
    this.endpointType = endpointType;
    return this;
  }

  public AbstractPipelinesVerbatimMessage setExtraPath(String extraPath) {
    this.extraPath = extraPath;
    return this;
  }

  public AbstractPipelinesVerbatimMessage setValidationResult(ValidationResult validationResult) {
    this.validationResult = validationResult;
    return this;
  }

  public AbstractPipelinesVerbatimMessage setResetPrefix(String resetPrefix) {
    this.resetPrefix = resetPrefix;
    return this;
  }

  public DatasetType getDatasetType() {
    return datasetType;
  }

  public AbstractPipelinesVerbatimMessage setDatasetType(DatasetType datasetType) {
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
    AbstractPipelinesVerbatimMessage that = (AbstractPipelinesVerbatimMessage) o;
    return Objects.equals(datasetUuid, that.datasetUuid)
        && Objects.equals(attempt, that.attempt)
        && Objects.equals(interpretTypes, that.interpretTypes)
        && Objects.equals(pipelineSteps, that.pipelineSteps)
        && Objects.equals(runner, that.runner)
        && endpointType == that.endpointType
        && Objects.equals(extraPath, that.extraPath)
        && Objects.equals(validationResult, that.validationResult)
        && Objects.equals(resetPrefix, that.resetPrefix)
        && Objects.equals(executionId, that.executionId)
        && datasetType == that.datasetType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        datasetUuid,
        attempt,
        interpretTypes,
        pipelineSteps,
        runner,
        endpointType,
        extraPath,
        validationResult,
        resetPrefix,
        executionId,
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
