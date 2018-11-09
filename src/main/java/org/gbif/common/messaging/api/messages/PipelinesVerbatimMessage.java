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
 * Message is published when the conversion from of dataset from various formats(DwC or Xml) to avro(ExtendedRecord) is done.
 */
public class PipelinesVerbatimMessage implements PipelineBasedMessage {

  public static final String ROUTING_KEY = "crawl.pipelines.verbatim.finished";

  private final UUID datasetUuid;
  private final int attempt;
  private final Set<String> interpretTypes;
  private final Set<String> pipelineSteps;

  @JsonCreator
  public PipelinesVerbatimMessage(@JsonProperty("datasetUuid") UUID datasetUuid, @JsonProperty("attempt") int attempt,
    @JsonProperty("interpretTypes") Set<String> interpretTypes,
    @JsonProperty("pipelineSteps") Set<String> pipelineSteps) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    this.interpretTypes = checkNotNull(interpretTypes, "interpretTypes can't be null");
    checkArgument(attempt > 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    this.pipelineSteps = pipelineSteps == null ? Collections.emptySet() : pipelineSteps;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PipelinesVerbatimMessage that = (PipelinesVerbatimMessage) o;
    return attempt == that.attempt && Objects.equals(datasetUuid, that.datasetUuid) && Objects.equals(interpretTypes,
      that.interpretTypes) && Objects.equals(pipelineSteps, that.pipelineSteps);
  }

  @Override
  public int hashCode() {
    return Objects.hash(datasetUuid, attempt, interpretTypes, pipelineSteps);
  }

  @Override
  public String toString() {
    return "PipelinesVerbatimMessage{" + "datasetUuid=" + datasetUuid + ", attempt=" + attempt + ", interpretTypes="
           + interpretTypes + ", pipelineSteps=" + pipelineSteps + '}';
  }
}
