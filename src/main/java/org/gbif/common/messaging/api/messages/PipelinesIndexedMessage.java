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
 * This message instructs the dataset mutator service to send IndexDatasetMessage for each occurrence in the
 * dataset.
 */
public class PipelinesIndexedMessage implements PipelineBasedMessage {

  public static final String ROUTING_KEY = "occurrence.pipelines.indexing.finished";

  private UUID datasetUuid;
  private int attempt;
  private Set<String> pipelineSteps;
  private String runner;

  public PipelinesIndexedMessage() {
  }

  @JsonCreator
  public PipelinesIndexedMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("attempt") int attempt,
      @JsonProperty("pipelineSteps") Set<String> pipelineSteps,
      @JsonProperty("runner") String runner) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    checkArgument(attempt >= 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    this.pipelineSteps = pipelineSteps == null ? Collections.emptySet() : pipelineSteps;
    this.runner = runner;
  }

  public PipelinesIndexedMessage(
      UUID datasetUuid,
      int attempt,
      Set<String> pipelineSteps) {
    this(datasetUuid, attempt, pipelineSteps, null);
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

  public PipelinesIndexedMessage setDatasetUuid(UUID datasetUuid) {
    this.datasetUuid = datasetUuid;
    return this;
  }

  public PipelinesIndexedMessage setAttempt(int attempt) {
    this.attempt = attempt;
    return this;
  }

  public PipelinesIndexedMessage setPipelineSteps(Set<String> pipelineSteps) {
    this.pipelineSteps = pipelineSteps;
    return this;
  }

  public PipelinesIndexedMessage setRunner(String runner) {
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
    PipelinesIndexedMessage that = (PipelinesIndexedMessage) o;
    return attempt == that.attempt &&
        Objects.equals(datasetUuid, that.datasetUuid) &&
        Objects.equals(pipelineSteps, that.pipelineSteps) &&
        Objects.equals(runner, that.runner);
  }

  @Override
  public int hashCode() {
    return Objects.hash(datasetUuid, attempt, pipelineSteps, runner);
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
