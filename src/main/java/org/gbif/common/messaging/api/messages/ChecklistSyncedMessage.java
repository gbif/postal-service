package org.gbif.common.messaging.api.messages;

import java.util.UUID;

import com.google.common.base.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The message sent whenever an entire checklist is imported into checklistbank.
 */
public class ChecklistSyncedMessage implements DatasetBasedMessage {
  public static final String ROUTING_KEY = "checklist.imported";

  private final UUID datasetUuid;

  @JsonCreator
  public ChecklistSyncedMessage(
    @JsonProperty("datasetUuid") UUID datasetUuid
  ) {
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
    final ChecklistSyncedMessage other = (ChecklistSyncedMessage) obj;
    return Objects.equal(this.datasetUuid, other.datasetUuid);
  }
}
