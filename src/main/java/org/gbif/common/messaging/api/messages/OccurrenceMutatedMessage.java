package org.gbif.common.messaging.api.messages;

import org.gbif.api.model.occurrence.Occurrence;
import org.gbif.api.vocabulary.OccurrencePersistenceStatus;

import java.util.UUID;
import javax.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The message sent whenever an "interpreted" occurrence has changed (either NEW, UPDATED, or DELETED). An interpreted
 * occurrence is the result of interpreting the values of a verbatim occurrence.
 */
public class OccurrenceMutatedMessage implements DatasetBasedMessage {

  private final UUID datasetUuid;
  // the crawl attempt id - only meaningful for NEW and UPDATE
  private final Integer crawlAttempt;
  private final OccurrencePersistenceStatus status;
  private final Occurrence oldOccurrence;
  private final Occurrence newOccurrence;

  // only used for deletions
  // if this is a deletion, the reason it has been deleted
  private final OccurrenceDeletionReason deletionReason;
  // the last crawl attempt in which this occurrence was seen
  private final Integer crawlAttemptLastSeen;
  // the latest crawl attempt for this occurrence's dataset
  private final Integer latestCrawlAttemptForDataset;

  @JsonCreator
  public OccurrenceMutatedMessage(@JsonProperty("datasetUuid") UUID datasetUuid,
    @JsonProperty("status") OccurrencePersistenceStatus status,
    @Nullable @JsonProperty("crawlAttempt") Integer crawlAttempt,
    @Nullable @JsonProperty("oldOccurrence") Occurrence oldOccurrence,
    @Nullable @JsonProperty("newOccurrence") Occurrence newOccurrence,
    @Nullable @JsonProperty("deletionReason") OccurrenceDeletionReason deletionReason,
    @Nullable @JsonProperty("crawlAttemptLastSeen") Integer crawlAttemptLastSeen,
    @Nullable @JsonProperty("latestCrawlAttemptForDataset") Integer latestCrawlAttemptForDataset) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    this.status = checkNotNull(status, "status can't be null");
    switch (status) {
      case NEW:
        checkNotNull(newOccurrence, "newOccurrence can't be null");
        checkArgument(crawlAttempt != null && crawlAttempt > 0, "attempt must be greater than 0");
        break;
      case UPDATED:
        checkNotNull(newOccurrence, "newOccurrence can't be null for updates");
        checkNotNull(oldOccurrence, "oldOccurrence can't be null for updates");
        checkArgument(crawlAttempt != null && crawlAttempt > 0, "attempt must be greater than 0");
        break;
      case DELETED:
        checkNotNull(oldOccurrence, "oldOccurrence can't be null for deletes");
        checkNotNull(deletionReason, "deletionReason can't be null for deletes");
        if (deletionReason == OccurrenceDeletionReason.NOT_SEEN_IN_LAST_CRAWL) {
          checkArgument(crawlAttemptLastSeen != null && crawlAttemptLastSeen > 0,
            "crawlAttemptLastSeen must be greater than 0");
          checkArgument(latestCrawlAttemptForDataset != null && latestCrawlAttemptForDataset > 0,
            "latestCrawlAttemptForDataset must be greater than 0");
        }
        break;
      case UNCHANGED: // should never be seen
        break;
    }
    this.crawlAttempt = crawlAttempt;
    this.newOccurrence = newOccurrence;
    this.oldOccurrence = oldOccurrence;
    this.deletionReason = deletionReason;
    this.crawlAttemptLastSeen = crawlAttemptLastSeen;
    this.latestCrawlAttemptForDataset = latestCrawlAttemptForDataset;
  }

  public static OccurrenceMutatedMessage buildNewMessage(UUID datasetUuid, Occurrence newOccurrence, int crawlAttempt) {
    return new OccurrenceMutatedMessage(datasetUuid, OccurrencePersistenceStatus.NEW, crawlAttempt, null, newOccurrence,
      null, null, null);
  }

  public static OccurrenceMutatedMessage buildUpdateMessage(UUID datasetUuid, Occurrence oldOccurrence,
    Occurrence newOccurrence, int crawlAttempt) {
    return new OccurrenceMutatedMessage(datasetUuid, OccurrencePersistenceStatus.UPDATED, crawlAttempt, oldOccurrence,
      newOccurrence, null, null, null);
  }

  public static OccurrenceMutatedMessage buildDeleteMessage(UUID datasetUuid, Occurrence oldOccurrence,
    OccurrenceDeletionReason reason, Integer crawlAttemptLastSeen, Integer lastCrawlAttemptForDataset) {
    return new OccurrenceMutatedMessage(datasetUuid, OccurrencePersistenceStatus.DELETED, null, oldOccurrence, null,
      reason, crawlAttemptLastSeen, lastCrawlAttemptForDataset);
  }

  @Override
  public String getRoutingKey() {
    return "occurrence.interpreted.mutated";
  }

  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  public Integer getCrawlAttempt() {
    return crawlAttempt;
  }

  public OccurrencePersistenceStatus getStatus() {
    return status;
  }

  public Occurrence getOldOccurrence() {
    return oldOccurrence;
  }

  public Occurrence getNewOccurrence() {
    return newOccurrence;
  }

  public OccurrenceDeletionReason getDeletionReason() {
    return deletionReason;
  }

  public Integer getCrawlAttemptLastSeen() {
    return crawlAttemptLastSeen;
  }

  public Integer getLatestCrawlAttemptForDataset() {
    return latestCrawlAttemptForDataset;
  }

  @Override
  public int hashCode() {
    return Objects
      .hashCode(datasetUuid, crawlAttempt, status, oldOccurrence, newOccurrence, deletionReason, crawlAttemptLastSeen,
        latestCrawlAttemptForDataset);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final OccurrenceMutatedMessage other = (OccurrenceMutatedMessage) obj;
    return Objects.equal(this.datasetUuid, other.datasetUuid) && Objects.equal(this.crawlAttempt, other.crawlAttempt)
           && Objects.equal(this.status, other.status) && Objects.equal(this.oldOccurrence, other.oldOccurrence)
           && Objects.equal(this.newOccurrence, other.newOccurrence) && Objects
      .equal(this.deletionReason, other.deletionReason) && Objects
             .equal(this.crawlAttemptLastSeen, other.crawlAttemptLastSeen) && Objects
             .equal(this.latestCrawlAttemptForDataset, other.latestCrawlAttemptForDataset);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("datasetUuid", datasetUuid).add("crawlAttempt", crawlAttempt)
      .add("status", status).add("oldOccurrence", oldOccurrence).add("newOccurrence", newOccurrence)
      .add("deletionReason", deletionReason).add("crawlAttemptLastSeen", crawlAttemptLastSeen)
      .add("latestCrawlAttemptForDataset", latestCrawlAttemptForDataset).toString();
  }
}
