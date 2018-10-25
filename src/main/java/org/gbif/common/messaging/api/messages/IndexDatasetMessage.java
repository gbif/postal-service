package org.gbif.common.messaging.api.messages;

import java.util.Objects;
import java.util.UUID;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This message instructs the dataset mutator service to send InterpretDatasetMessage  for each occurrence in the
 * dataset.
 */
public class IndexDatasetMessage implements DatasetBasedMessage {

  private final UUID datasetUuid;
  private final int attempt;

  @JsonCreator
  public IndexDatasetMessage(@JsonProperty("datasetUuid") UUID datasetUuid, @JsonProperty("attempt") int attempt) {
    this.datasetUuid = datasetUuid;
    this.attempt = attempt;
  }

  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  public int getAttempt() {
    return attempt;
  }

  @Override
  public String getRoutingKey() {
    return "occurrence.index.dataset";
  }

  @Override
  public int hashCode() {
    return Objects.hash(datasetUuid, attempt);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IndexDatasetMessage that = (IndexDatasetMessage) o;
    return attempt == that.attempt && Objects.equals(datasetUuid, that.datasetUuid);
  }
}
