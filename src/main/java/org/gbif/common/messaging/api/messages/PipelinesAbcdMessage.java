package org.gbif.common.messaging.api.messages;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.gbif.api.vocabulary.EndpointType;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

import javax.annotation.Nullable;

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
  @JsonProperty("lastModified")
  private Date lastModified;
  private boolean modified;
  private Set<String> pipelineSteps;
  private EndpointType endpointType;

  @JsonCreator
  @com.fasterxml.jackson.annotation.JsonCreator
  public PipelinesAbcdMessage(
      @com.fasterxml.jackson.annotation.JsonProperty("datasetUuid") @JsonProperty("datasetUuid") UUID datasetUuid,
      @com.fasterxml.jackson.annotation.JsonProperty("source") @JsonProperty("source") URI source,
      @com.fasterxml.jackson.annotation.JsonProperty("attempt") @JsonProperty("attempt") int attempt,
      @com.fasterxml.jackson.annotation.JsonProperty("lastModified") @Nullable @JsonProperty("lastModified") Date lastModified,
      @com.fasterxml.jackson.annotation.JsonProperty("modified") @JsonProperty("modified") boolean modified,
      @com.fasterxml.jackson.annotation.JsonProperty("pipelineSteps") @JsonProperty("pipelineSteps") Set<String> pipelineSteps,
      @com.fasterxml.jackson.annotation.JsonProperty("endpointType") @JsonProperty("endpointType") EndpointType endpointType) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    this.source = checkNotNull(source, "source can't be null");
    checkArgument(attempt > 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    this.lastModified = lastModified;
    this.modified = modified;
    this.pipelineSteps = pipelineSteps == null ? Collections.emptySet() : pipelineSteps;
    this.endpointType = endpointType;
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

  public EndpointType getEndpointType() {
    return endpointType;
  }

  /**
   * @return the date the downloaded archive was last modified or null e.g. for failed downloads
   */
  @Nullable
  @JsonIgnore
  public Optional<Date> getLastModified() {
    return Optional.ofNullable(lastModified);
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

  public PipelinesAbcdMessage setLastModified(Date lastModified) {
    this.lastModified = lastModified;
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
        Objects.equals(lastModified, that.lastModified) &&
        Objects.equals(pipelineSteps, that.pipelineSteps) &&
        endpointType == that.endpointType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(datasetUuid, source, attempt, lastModified, modified, pipelineSteps, endpointType);
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
