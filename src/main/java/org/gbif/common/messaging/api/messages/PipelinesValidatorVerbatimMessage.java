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

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Similar to {@link PipelinesVerbatimMessage} but specifically for the Pipelines DwC-A Validator.
 */
public class PipelinesValidatorVerbatimMessage implements PipelineBasedMessage, PipelinesRunnerMessage {

  public static final String ROUTING_KEY = "occurrence.pipelines.verbatim.finished.validator";

  private UUID datasetUuid;
  private Integer attempt;
  private Set<String> interpretTypes;
  private Set<String> pipelineSteps;
  private String runner;
  private EndpointType endpointType;
  private String extraPath;
  private ValidationResult validationResult;
  private String resetPrefix;
  private Long executionId;
  private DatasetType datasetType;

  public PipelinesValidatorVerbatimMessage() {}

  @JsonCreator
  public PipelinesValidatorVerbatimMessage(
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
  public String getRoutingKey() {
    String key = ROUTING_KEY;
    if (runner != null && !runner.isEmpty()) {
      key = key + "." + runner.toLowerCase();
    }
    return key;
  }

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

  public PipelinesValidatorVerbatimMessage setDatasetUuid(UUID datasetUuid) {
    this.datasetUuid = datasetUuid;
    return this;
  }

  public PipelinesValidatorVerbatimMessage setAttempt(Integer attempt) {
    this.attempt = attempt;
    return this;
  }

  public PipelinesValidatorVerbatimMessage setInterpretTypes(Set<String> interpretTypes) {
    this.interpretTypes = interpretTypes;
    return this;
  }

  public PipelinesValidatorVerbatimMessage setPipelineSteps(Set<String> pipelineSteps) {
    this.pipelineSteps = pipelineSteps;
    return this;
  }

  public PipelinesValidatorVerbatimMessage setRunner(String runner) {
    this.runner = runner;
    return this;
  }

  public PipelinesValidatorVerbatimMessage setEndpointType(EndpointType endpointType) {
    this.endpointType = endpointType;
    return this;
  }

  public PipelinesValidatorVerbatimMessage setExtraPath(String extraPath) {
    this.extraPath = extraPath;
    return this;
  }

  public PipelinesValidatorVerbatimMessage setValidationResult(ValidationResult validationResult) {
    this.validationResult = validationResult;
    return this;
  }

  public PipelinesValidatorVerbatimMessage setResetPrefix(String resetPrefix) {
    this.resetPrefix = resetPrefix;
    return this;
  }

  public DatasetType getDatasetType() {
    return datasetType;
  }

  public PipelinesValidatorVerbatimMessage setDatasetType(DatasetType datasetType) {
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
    PipelinesValidatorVerbatimMessage that = (PipelinesValidatorVerbatimMessage) o;
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

    public ValidationResult setTripletValid(boolean tripletValid) {
      this.tripletValid = tripletValid;
      return this;
    }

    public ValidationResult setOccurrenceIdValid(boolean occurrenceIdValid) {
      this.occurrenceIdValid = occurrenceIdValid;
      return this;
    }

    public ValidationResult setUseExtendedRecordId(Boolean useExtendedRecordId) {
      this.useExtendedRecordId = useExtendedRecordId;
      return this;
    }

    public ValidationResult setNumberOfRecords(Long numberOfRecords) {
      this.numberOfRecords = numberOfRecords;
      return this;
    }

    public ValidationResult setNumberOfEventRecords(Long numberOfEventRecords) {
      this.numberOfEventRecords = numberOfEventRecords;
      return this;
    }

    public boolean isTripletValid() {
      return tripletValid;
    }

    public boolean isOccurrenceIdValid() {
      return occurrenceIdValid;
    }

    public Boolean isUseExtendedRecordId() {
      return useExtendedRecordId;
    }

    public Long getNumberOfRecords() {
      return numberOfRecords;
    }

    public Long getNumberOfEventRecords() {
      return numberOfEventRecords;
    }
  }
}
