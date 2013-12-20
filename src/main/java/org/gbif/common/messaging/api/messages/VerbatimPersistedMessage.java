package org.gbif.common.messaging.api.messages;

import org.gbif.api.model.occurrence.OccurrencePersistenceStatus;

import java.util.UUID;

import com.google.common.base.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The message sent whenever a "verbatim" occurrence is persisted. A verbatim occurrence is the result of parsing
 * incoming fragments, but has had no interpretation (e.g. dates, co-ordinates, etc) applied. Note that this message
 * will only have status NEW or UPDATED - never UNCHANGED.
 */
public class VerbatimPersistedMessage implements DatasetBasedMessage {

  private final UUID datasetUuid;

  private final int attempt;

  private final OccurrencePersistenceStatus status;

  private final int occurrenceKey;

  @JsonCreator
  public VerbatimPersistedMessage(
    @JsonProperty("datasetUuid") UUID datasetUuid,
    @JsonProperty("attempt") int attempt,
    @JsonProperty("status") OccurrencePersistenceStatus status,
    @JsonProperty("occurrenceKey") int occurrenceKey
  ) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    checkArgument(attempt > 0, "attempt must be greater than 0");
    this.attempt = attempt;
    this.status = checkNotNull(status, "status can't be null");
    this.occurrenceKey = checkNotNull(occurrenceKey, "occurrenceKey can't be null");
  }

  @Override
  public String getRoutingKey() {
    return "occurrence.verbatim.persisted";
  }

  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  public int getAttempt() {
    return attempt;
  }

  public OccurrencePersistenceStatus getStatus() {
    return status;
  }

  public int getOccurrenceKey() {
    return occurrenceKey;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(datasetUuid, attempt, status, occurrenceKey);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final VerbatimPersistedMessage other = (VerbatimPersistedMessage) obj;
    return Objects.equal(this.datasetUuid, other.datasetUuid)
           && Objects.equal(this.attempt, other.attempt)
           && Objects.equal(this.status, other.status)
           && Objects.equal(this.occurrenceKey, other.occurrenceKey);
  }
}
