package org.gbif.common.messaging.api.messages;

import java.util.UUID;

import com.google.common.base.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This message instructs the dataset mutator service to send ParseFragmentMessages for each occurrence in the
 * dataset.
 */
public class ParseDatasetMessage implements DatasetBasedMessage {

  private final UUID datasetUuid;

  @JsonCreator
  public ParseDatasetMessage(@JsonProperty("datasetUuid") UUID datasetUuid) {
    this.datasetUuid = datasetUuid;
  }

  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  @Override
  public String getRoutingKey() {
    return "occurrence.parse.dataset";
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
    final ParseDatasetMessage other = (ParseDatasetMessage) obj;
    return Objects.equal(this.datasetUuid, other.datasetUuid);
  }
}
