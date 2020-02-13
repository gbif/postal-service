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

import org.gbif.api.vocabulary.EndpointType;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Message is published when the conversion from of dataset from various formats(DwC or Xml) to
 * avro(ExtendedRecord) is done.
 */
public class PipelinesVerbatimMessage implements PipelineBasedMessage {

  public static final String ROUTING_KEY = "occurrence.pipelines.verbatim.finished";

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
      @JsonProperty("executionId") Long executionId) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    this.interpretTypes = checkNotNull(interpretTypes, "interpretTypes can't be null");
    this.attempt = attempt;
    this.pipelineSteps = pipelineSteps == null ? Collections.emptySet() : pipelineSteps;
    this.runner = runner;
    this.endpointType = endpointType;
    this.extraPath = extraPath;
    this.validationResult = validationResult;
    this.resetPrefix = resetPrefix;
    this.executionId = executionId;
  }

  public PipelinesVerbatimMessage(
      UUID datasetUuid,
      Integer attempt,
      Set<String> interpretTypes,
      Set<String> pipelineSteps,
      EndpointType endpointType,
      ValidationResult validationResult) {
    this(
        datasetUuid,
        attempt,
        interpretTypes,
        pipelineSteps,
        null,
        endpointType,
        null,
        validationResult,
        null,
        null);
  }

  public PipelinesVerbatimMessage(
      UUID datasetUuid,
      Integer attempt,
      Set<String> interpretTypes,
      Set<String> pipelineSteps,
      EndpointType endpointType) {
    this(
        datasetUuid,
        attempt,
        interpretTypes,
        pipelineSteps,
        null,
        endpointType,
        null,
        new ValidationResult(true, true, null, null),
        null,
        null);
  }

  public PipelinesVerbatimMessage(
      UUID datasetUuid,
      Set<String> interpretTypes,
      Set<String> pipelineSteps,
      EndpointType endpointType) {
    this(datasetUuid, null, interpretTypes, pipelineSteps, endpointType);
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
    return ROUTING_KEY;
  }

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

  public PipelinesVerbatimMessage setDatasetUuid(UUID datasetUuid) {
    this.datasetUuid = datasetUuid;
    return this;
  }

  public PipelinesVerbatimMessage setAttempt(Integer attempt) {
    this.attempt = attempt;
    return this;
  }

  public PipelinesVerbatimMessage setInterpretTypes(Set<String> interpretTypes) {
    this.interpretTypes = interpretTypes;
    return this;
  }

  public PipelinesVerbatimMessage setPipelineSteps(Set<String> pipelineSteps) {
    this.pipelineSteps = pipelineSteps;
    return this;
  }

  public PipelinesVerbatimMessage setRunner(String runner) {
    this.runner = runner;
    return this;
  }

  public PipelinesVerbatimMessage setEndpointType(EndpointType endpointType) {
    this.endpointType = endpointType;
    return this;
  }

  public PipelinesVerbatimMessage setExtraPath(String extraPath) {
    this.extraPath = extraPath;
    return this;
  }

  public PipelinesVerbatimMessage setValidationResult(ValidationResult validationResult) {
    this.validationResult = validationResult;
    return this;
  }

  public PipelinesVerbatimMessage setResetPrefix(String resetPrefix) {
    this.resetPrefix = resetPrefix;
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
    PipelinesVerbatimMessage that = (PipelinesVerbatimMessage) o;
    return Objects.equals(datasetUuid, that.datasetUuid)
        && Objects.equals(attempt, that.attempt)
        && Objects.equals(interpretTypes, that.interpretTypes)
        && Objects.equals(pipelineSteps, that.pipelineSteps)
        && Objects.equals(runner, that.runner)
        && endpointType == that.endpointType
        && Objects.equals(extraPath, that.extraPath)
        && Objects.equals(validationResult, that.validationResult)
        && Objects.equals(resetPrefix, that.resetPrefix)
        && Objects.equals(executionId, that.executionId);
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

  public static class ValidationResult {

    private boolean tripletValid;
    private boolean occurrenceIdValid;
    private Boolean useExtendedRecordId;
    private Long numberOfRecords;

    public ValidationResult() {}

    @JsonCreator
    public ValidationResult(
        @JsonProperty("tripletValid") boolean tripletValid,
        @JsonProperty("occurrenceIdValid") boolean occurrenceIdValid,
        @JsonProperty("useExtendedRecordId") Boolean useExtendedRecordId,
        @JsonProperty("numberOfRecords") Long numberOfRecords) {
      this.tripletValid = tripletValid;
      this.occurrenceIdValid = occurrenceIdValid;
      this.useExtendedRecordId = useExtendedRecordId;
      this.numberOfRecords = numberOfRecords;
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
  }
}
