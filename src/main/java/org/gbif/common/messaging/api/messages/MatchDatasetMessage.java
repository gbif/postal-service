package org.gbif.common.messaging.api.messages;

import java.util.UUID;

import com.google.common.base.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Indicates that a dataset should be rematched to the backbone
 */
public class MatchDatasetMessage implements DatasetBasedMessage {
  public static final String ROUTING_KEY = "dataset.match";

  private final UUID datasetKey;

  @JsonCreator
  public MatchDatasetMessage(@JsonProperty("datasetKey") UUID datasetKey) {
    this.datasetKey = checkNotNull(datasetKey, "datasetKey can't be null");
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }

  @Override
  public UUID getDatasetUuid() {
    return datasetKey;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(datasetKey);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (MatchDatasetMessage.class != obj.getClass()) {
      return false;
    }
    final MatchDatasetMessage other = (MatchDatasetMessage) obj;
    return Objects.equal(this.datasetKey, other.datasetKey);
  }
}
