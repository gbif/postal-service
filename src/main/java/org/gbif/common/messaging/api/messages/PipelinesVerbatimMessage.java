package org.gbif.common.messaging.api.messages;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.gbif.api.vocabulary.EndpointType;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Message is published when the conversion from of dataset from various formats(DwC or Xml) to avro(ExtendedRecord) is
 * done.
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

  public PipelinesVerbatimMessage() {
  }

  @JsonCreator
  @com.fasterxml.jackson.annotation.JsonCreator
  public PipelinesVerbatimMessage(
      @com.fasterxml.jackson.annotation.JsonProperty("datasetUuid") @JsonProperty("datasetUuid") UUID datasetUuid,
      @com.fasterxml.jackson.annotation.JsonProperty("attempt") @JsonProperty("attempt") Integer attempt,
      @com.fasterxml.jackson.annotation.JsonProperty("interpretTypes") @JsonProperty("interpretTypes") Set<String> interpretTypes,
      @com.fasterxml.jackson.annotation.JsonProperty("pipelineSteps") @JsonProperty("pipelineSteps") Set<String> pipelineSteps,
      @com.fasterxml.jackson.annotation.JsonProperty("runner") @JsonProperty("runner") String runner,
      @com.fasterxml.jackson.annotation.JsonProperty("endpointType") @JsonProperty("endpointType") EndpointType endpointType,
      @com.fasterxml.jackson.annotation.JsonProperty("extraPath") @JsonProperty("extraPath") String extraPath,
      @com.fasterxml.jackson.annotation.JsonProperty("validationResult") @JsonProperty("validationResult") ValidationResult validationResult,
      @com.fasterxml.jackson.annotation.JsonProperty("resetPrefix") @JsonProperty("resetPrefix") String resetPrefix) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    this.interpretTypes = checkNotNull(interpretTypes, "interpretTypes can't be null");
    this.attempt = attempt;
    this.pipelineSteps = pipelineSteps == null ? Collections.emptySet() : pipelineSteps;
    this.runner = runner;
    this.endpointType = endpointType;
    this.extraPath = extraPath;
    this.validationResult = validationResult;
    this.resetPrefix = resetPrefix;
  }

  public PipelinesVerbatimMessage(
      UUID datasetUuid,
      Integer attempt,
      Set<String> interpretTypes,
      Set<String> pipelineSteps,
      EndpointType endpointType,
      ValidationResult validationResult) {
    this(datasetUuid, attempt, interpretTypes, pipelineSteps, null, endpointType, null, validationResult, null);
  }

  public PipelinesVerbatimMessage(
      UUID datasetUuid,
      Integer attempt,
      Set<String> interpretTypes,
      Set<String> pipelineSteps,
      EndpointType endpointType) {
    this(datasetUuid, attempt, interpretTypes, pipelineSteps, null, endpointType, null,
        new ValidationResult(true, true, null, null), null);
  }

  public PipelinesVerbatimMessage(
      UUID datasetUuid,
      Set<String> interpretTypes,
      Set<String> pipelineSteps,
      EndpointType endpointType) {
    this(datasetUuid, null, interpretTypes, pipelineSteps, endpointType);
  }

  /**
   * @return datasetUUID for the converted dataset
   */
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  /**
   * @return attempt for the converted dataset
   */
  @Override
  public Integer getAttempt() {
    return attempt;
  }

  @Override
  public Set<String> getPipelineSteps() {
    return pipelineSteps;
  }

  /**
   * @return types of interpretation - ALL, LOCATION, BASE or etc.
   */
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
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PipelinesVerbatimMessage that = (PipelinesVerbatimMessage) o;
    return Objects.equals(datasetUuid, that.datasetUuid) &&
        Objects.equals(attempt, that.attempt) &&
        Objects.equals(interpretTypes, that.interpretTypes) &&
        Objects.equals(pipelineSteps, that.pipelineSteps) &&
        Objects.equals(runner, that.runner) &&
        endpointType == that.endpointType &&
        Objects.equals(extraPath, that.extraPath) &&
        Objects.equals(validationResult, that.validationResult) &&
        Objects.equals(resetPrefix, that.resetPrefix);
  }

  @Override
  public int hashCode() {
    return Objects.hash(datasetUuid, attempt, interpretTypes, pipelineSteps, runner, endpointType, extraPath,
        validationResult, resetPrefix);
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

    public ValidationResult() {
    }

    @JsonCreator
    public ValidationResult(
        @JsonProperty("tripletValid") boolean tripletValid,
        @JsonProperty("occurrenceIdValid") boolean occurrenceIdValid,
        @JsonProperty("useExtendedRecordId") Boolean useExtendedRecordId,
        @JsonProperty("numberOfRecords") Long numberOfRecords
    ) {
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
