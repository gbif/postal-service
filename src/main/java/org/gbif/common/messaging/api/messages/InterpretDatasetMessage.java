package org.gbif.common.messaging.api.messages;

import java.util.UUID;

import com.google.common.base.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This message instructs the dataset mutator service to send InterpretVerbatimMessages for each occurrence in the
 * dataset.
 */
public class InterpretDatasetMessage implements DatasetBasedMessage {

  private final UUID datasetUuid;

  @JsonCreator
  public InterpretDatasetMessage(@JsonProperty("datasetUuid") UUID datasetUuid) {
    this.datasetUuid = datasetUuid;
  }

  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  @Override
  public String getRoutingKey() {
    return "occurrence.interpret.dataset";
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(datasetUuid);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final InterpretDatasetMessage other = (InterpretDatasetMessage) obj;
    return Objects.equal(this.datasetUuid, other.datasetUuid);
  }
}
