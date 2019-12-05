package org.gbif.common.messaging.api.messages;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.gbif.api.vocabulary.EndpointType;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * We send this every time an ABCD archive has been downloaded.
 * This includes cases when the archive hasn't been modified since we last downloaded it.
 */
public class PipelinesAbcdMessage implements PipelineBasedMessage {

  public static final String ROUTING_KEY = AbcdaPagingFinishedMessage.ROUTING_KEY;

  private UUID datasetUuid;
  private URI source;
  private int attempt;
  private boolean modified;
  private Set<String> pipelineSteps;
  private EndpointType endpointType;
  private Long executionId;

  @JsonCreator
  public PipelinesAbcdMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("source") URI source,
      @JsonProperty("attempt") int attempt,
      @JsonProperty("modified") boolean modified,
      @JsonProperty("pipelineSteps") Set<String> pipelineSteps,
      @JsonProperty("endpointType") EndpointType endpointType,
      @JsonProperty("executionId") Long executionId) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    this.source = checkNotNull(source, "source can't be null");
    checkArgument(attempt > 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    this.modified = modified;
    this.pipelineSteps = pipelineSteps == null ? Collections.emptySet() : pipelineSteps;
    this.endpointType = endpointType;
    this.executionId = executionId;
  }

  /**
   * @return dataset uuid
   */
  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  /**
   * @return source the archive has been downloaded from
   */
  public URI getSource() {
    return source;
  }

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

  public EndpointType getEndpointType() {
    return endpointType;
  }

  /**
   * @return true if the archive has changed since we last downloaded it or never been downloaded before
   */
  public boolean isModified() {
    return modified;
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }

  public PipelinesAbcdMessage setDatasetUuid(UUID datasetUuid) {
    this.datasetUuid = datasetUuid;
    return this;
  }

  public PipelinesAbcdMessage setSource(URI source) {
    this.source = source;
    return this;
  }

  public PipelinesAbcdMessage setAttempt(int attempt) {
    this.attempt = attempt;
    return this;
  }

  public PipelinesAbcdMessage setModified(boolean modified) {
    this.modified = modified;
    return this;
  }

  public PipelinesAbcdMessage setPipelineSteps(Set<String> pipelineSteps) {
    this.pipelineSteps = pipelineSteps;
    return this;
  }

  public PipelinesAbcdMessage setEndpointType(EndpointType endpointType) {
    this.endpointType = endpointType;
    return this;
  }

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
    PipelinesAbcdMessage that = (PipelinesAbcdMessage) o;
    return attempt == that.attempt &&
        modified == that.modified &&
        Objects.equals(datasetUuid, that.datasetUuid) &&
        Objects.equals(source, that.source) &&
        Objects.equals(pipelineSteps, that.pipelineSteps) &&
        endpointType == that.endpointType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(datasetUuid, source, attempt, modified, pipelineSteps, endpointType);
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
