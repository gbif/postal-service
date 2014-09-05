package org.gbif.common.messaging.api.messages;

import org.gbif.api.model.crawler.NormalizerStats;

import java.util.UUID;

import com.google.common.base.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The message sent whenever an entire checklist is imported into neo and normalized.
 */
public class ChecklistNormalizedMessage implements DatasetBasedMessage {
  public static final String ROUTING_KEY = "checklist.normalized";

  private final UUID datasetUuid;
  private final NormalizerStats stats;

  @JsonCreator
  public ChecklistNormalizedMessage(
    @JsonProperty("datasetUuid") UUID datasetUuid,
    @JsonProperty("stats") NormalizerStats stats
  ) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    this.stats = checkNotNull(stats, "stats can't be null");
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }

  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  public NormalizerStats getStats() {
    return stats;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(datasetUuid, stats);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ChecklistNormalizedMessage other = (ChecklistNormalizedMessage) obj;
    return Objects.equal(this.datasetUuid, other.datasetUuid) && Objects.equal(this.stats, other.stats);
  }
}
