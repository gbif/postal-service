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
 * Message is published when the conversion from of dataset from various formats(DwC or Xml) to avro(ExtendedRecord) is
 * done.
 */
public class PipelinesVerbatimMessage implements PipelineBasedMessage {

  public static final String ROUTING_KEY = "crawl.pipelines.verbatim.finished";

  private UUID datasetUuid;
  private int attempt;
  private Set<String> interpretTypes;
  private Set<String> pipelineSteps;
  private String runner;

  public PipelinesVerbatimMessage() {
  }

  @JsonCreator
  public PipelinesVerbatimMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("attempt") int attempt,
      @JsonProperty("interpretTypes") Set<String> interpretTypes,
      @JsonProperty("pipelineSteps") Set<String> pipelineSteps,
      @JsonProperty("runner") String runner) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    this.interpretTypes = checkNotNull(interpretTypes, "interpretTypes can't be null");
    checkArgument(attempt > 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    this.pipelineSteps = pipelineSteps == null ? Collections.emptySet() : pipelineSteps;
    this.runner = runner;
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
  public int getAttempt() {
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

  public PipelinesVerbatimMessage setDatasetUuid(UUID datasetUuid) {
    this.datasetUuid = datasetUuid;
    return this;
  }

  public PipelinesVerbatimMessage setAttempt(int attempt) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PipelinesVerbatimMessage that = (PipelinesVerbatimMessage) o;
    return attempt == that.attempt &&
        Objects.equals(datasetUuid, that.datasetUuid) &&
        Objects.equals(interpretTypes, that.interpretTypes) &&
        Objects.equals(pipelineSteps, that.pipelineSteps) &&
        Objects.equals(runner, that.runner);
  }

  @Override
  public int hashCode() {
    return Objects.hash(datasetUuid, attempt, interpretTypes, pipelineSteps, runner);
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
