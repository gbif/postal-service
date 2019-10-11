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
 * This message instructs the dataset mutator service to send InterpretDatasetMessage for each occurrence in the
 * dataset.
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

  public PipelinesInterpretedMessage() {
  }

  @JsonCreator
  @com.fasterxml.jackson.annotation.JsonCreator
  public PipelinesInterpretedMessage(
      @com.fasterxml.jackson.annotation.JsonProperty("datasetUuid") @JsonProperty("datasetUuid") UUID datasetUuid,
      @com.fasterxml.jackson.annotation.JsonProperty("attempt") @JsonProperty("attempt") int attempt,
      @com.fasterxml.jackson.annotation.JsonProperty("pipelineSteps") @JsonProperty("pipelineSteps") Set<String> pipelineSteps,
      @com.fasterxml.jackson.annotation.JsonProperty("numberOfRecords") @JsonProperty("numberOfRecords") Long numberOfRecords,
      @com.fasterxml.jackson.annotation.JsonProperty("runner") @JsonProperty("runner") String runner,
      @com.fasterxml.jackson.annotation.JsonProperty("repeatAttempt") @JsonProperty("repeatAttempt") boolean repeatAttempt,
      @com.fasterxml.jackson.annotation.JsonProperty("resetPrefix") @JsonProperty("resetPrefix") String resetPrefix,
      @com.fasterxml.jackson.annotation.JsonProperty("onlyForStep") @JsonProperty("onlyForStep") String onlyForStep) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    checkArgument(attempt >= 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    this.pipelineSteps = pipelineSteps == null ? Collections.emptySet() : pipelineSteps;
    this.runner = runner;
    this.numberOfRecords = numberOfRecords;
    this.repeatAttempt = repeatAttempt;
    this.resetPrefix = resetPrefix;
    this.onlyForStep = onlyForStep;
  }

  public PipelinesInterpretedMessage(
      UUID datasetUuid,
      int attempt,
      Set<String> pipelineSteps,
      Long numberOfRecords,
      boolean repeatAttempt,
      String resetPrefix) {
    this(datasetUuid, attempt, pipelineSteps, numberOfRecords, null, repeatAttempt, resetPrefix, null);
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
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PipelinesInterpretedMessage that = (PipelinesInterpretedMessage) o;
    return attempt == that.attempt &&
        repeatAttempt == that.repeatAttempt &&
        Objects.equals(datasetUuid, that.datasetUuid) &&
        Objects.equals(pipelineSteps, that.pipelineSteps) &&
        Objects.equals(runner, that.runner) &&
        Objects.equals(numberOfRecords, that.numberOfRecords) &&
        Objects.equals(resetPrefix, that.resetPrefix) &&
        Objects.equals(onlyForStep, that.onlyForStep);
  }

  @Override
  public int hashCode() {
    return Objects.hash(datasetUuid, attempt, pipelineSteps, runner, numberOfRecords, repeatAttempt, resetPrefix,
        onlyForStep);
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
