package org.gbif.common.messaging.api.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The message sent whenever an entire checklist is imported into neo and normalized.
 */
public class ChecklistNormalizedMessage implements DatasetBasedMessage {
  public static final String ROUTING_KEY = "checklist.normalized";

  private final UUID datasetUuid;

  @JsonCreator
  public ChecklistNormalizedMessage(@JsonProperty("datasetUuid") UUID datasetUuid) {
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

  @Override
  public int hashCode() {
    return Objects.hashCode(datasetUuid);
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
    return Objects.equal(this.datasetUuid, other.datasetUuid);
  }
}
