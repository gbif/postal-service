package org.gbif.common.messaging.api.messages;

import java.util.Objects;
import java.util.UUID;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This message instructs the dataset mutator service to send IndexDatasetMessage for each occurrence in the
 * dataset.
 */
public class IndexComplitedMessage implements DatasetBasedMessage {

  public static final String ROUTING_KEY = "occurrence.index.complited";

  private final UUID datasetUuid;
  private final int attempt;

  @JsonCreator
  public IndexComplitedMessage(@JsonProperty("datasetUuid") UUID datasetUuid, @JsonProperty("attempt") int attempt) {
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
    return ROUTING_KEY;
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
    IndexComplitedMessage that = (IndexComplitedMessage) o;
    return attempt == that.attempt && Objects.equals(datasetUuid, that.datasetUuid);
  }
}
