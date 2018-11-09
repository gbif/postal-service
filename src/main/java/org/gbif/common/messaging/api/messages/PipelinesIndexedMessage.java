package org.gbif.common.messaging.api.messages;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This message instructs the dataset mutator service to send IndexDatasetMessage for each occurrence in the
 * dataset.
 */
public class PipelinesIndexedMessage implements PipelineBasedMessage {

  public static final String ROUTING_KEY = "occurrence.pipelines.indexing.complited";

  private final UUID datasetUuid;
  private final int attempt;
  private final Set<String> pipelineSteps;

  @JsonCreator
  public PipelinesIndexedMessage(@JsonProperty("datasetUuid") UUID datasetUuid, @JsonProperty("attempt") int attempt,
    @JsonProperty("pipelineSteps") Set<String> pipelineSteps) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    checkArgument(attempt > 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    this.pipelineSteps = pipelineSteps == null ? Collections.emptySet() : pipelineSteps;
  }

  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  @Override
  public int getAttempt() {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PipelinesIndexedMessage that = (PipelinesIndexedMessage) o;
    return attempt == that.attempt && Objects.equals(datasetUuid, that.datasetUuid) && Objects.equals(pipelineSteps,
      that.pipelineSteps);
  }

  @Override
  public int hashCode() {
    return Objects.hash(datasetUuid, attempt, pipelineSteps);
  }

  @Override
  public String toString() {
    return "PipelinesIndexedMessage{" + "datasetUuid=" + datasetUuid + ", attempt=" + attempt + ", pipelineSteps="
           + pipelineSteps + '}';
  }
}
