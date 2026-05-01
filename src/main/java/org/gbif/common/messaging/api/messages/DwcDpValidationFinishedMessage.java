package org.gbif.common.messaging.api.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import java.util.UUID;
import org.gbif.utils.PreconditionUtils;

public class DwcDpValidationFinishedMessage implements DatasetBasedMessage {

  public static final String ROUTING_KEY = "crawl.dwcdp.validation.finished";

  private final UUID datasetUuid;
  private final int attempt;

  @JsonCreator
  public DwcDpValidationFinishedMessage(
    @JsonProperty("datasetUuid") UUID datasetUuid,
    @JsonProperty("attempt") int attempt) {
    this.datasetUuid = Objects.requireNonNull(datasetUuid, "datasetUuid can't be null");
    PreconditionUtils.checkArgument(attempt > 0, "attempt has to be greater than 0");
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
}
