package org.gbif.common.messaging.api.messages;

import org.gbif.api.model.checklistbank.NameUsageMatch;

import java.util.Map;
import java.util.UUID;

import com.google.common.base.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The message sent whenever an entire checklist is matched to the GBIF backbone and stored in checklistbank.
 */
public class ChecklistMatchedMessage implements DatasetBasedMessage {
  public static final String ROUTING_KEY = "checklist.matched";

  private final UUID datasetUuid;
  private final Map<NameUsageMatch.MatchType, Integer> metrics;


  @JsonCreator
  public ChecklistMatchedMessage(@JsonProperty("datasetUuid") UUID datasetUuid,
    @JsonProperty("metrics") Map<NameUsageMatch.MatchType, Integer> metrics) {
    this.metrics = checkNotNull(metrics, "match metrics can't be null");
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }

  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  public Map<NameUsageMatch.MatchType, Integer> getMetrics() {
    return metrics;
  }

  public Integer getMatchCount(NameUsageMatch.MatchType matchType) {
    return metrics.get(matchType);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(datasetUuid, metrics);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ChecklistMatchedMessage other = (ChecklistMatchedMessage) obj;
    return Objects.equal(this.datasetUuid, other.datasetUuid)
      && Objects.equal(this.metrics, other.metrics);
  }
}
