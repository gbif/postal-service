package org.gbif.common.messaging.api.messages;

import org.gbif.api.vocabulary.OccurrencePersistenceStatus;

import java.util.UUID;

import com.google.common.base.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The message sent whenever an occurrence fragment is persisted. All incoming fragments will be persisted, hence there
 * will be one of these messages for every incoming fragment.
 */
public class FragmentPersistedMessage implements DatasetBasedMessage {

  private final UUID datasetUuid;

  private final int attempt;

  private final OccurrencePersistenceStatus status;

  private final long occurrenceKey;

  @JsonCreator
  public FragmentPersistedMessage(
    @JsonProperty("datasetUuid") UUID datasetUuid,
    @JsonProperty("attempt") int attempt,
    @JsonProperty("status") OccurrencePersistenceStatus status,
    @JsonProperty("occurrenceKey") long occurrenceKey
  ) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    checkArgument(attempt > 0, "attempt must be greater than 0");
    this.attempt = attempt;
    this.status = checkNotNull(status, "status can't be null");
    this.occurrenceKey = checkNotNull(occurrenceKey, "occurrenceKey can't be null");
  }

  @Override
  public String getRoutingKey() {
    return "occurrence.fragment.persisted";
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

  public long getOccurrenceKey() {
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
    final FragmentPersistedMessage other = (FragmentPersistedMessage) obj;
    return Objects.equal(this.datasetUuid, other.datasetUuid)
           && Objects.equal(this.attempt, other.attempt)
           && Objects.equal(this.status, other.status)
           && Objects.equal(this.occurrenceKey, other.occurrenceKey);
  }
}
