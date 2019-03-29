package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Message;

import javax.annotation.Nullable;

import com.google.common.base.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This message instructs the occurrence deletion service to delete a single Occurrence.
 */
public class DeleteOccurrenceMessage implements Message {

  private final long occurrenceKey;
  private final OccurrenceDeletionReason deletionReason;

  // only used when deletionReason is NOT_SEEN_IN_LAST_CRAWL
  // the last crawl attempt in which this occurrence was seen
  private final Integer crawlAttemptLastSeen;
  // the latest crawl attempt for this occurrence's dataset
  private final Integer latestCrawlAttemptForDataset;

  @JsonCreator
  public DeleteOccurrenceMessage(
    @JsonProperty("occurrenceKey") long occurrenceKey,
    @JsonProperty("deletionReason") OccurrenceDeletionReason deletionReason,
    @Nullable @JsonProperty("crawlAttemptLastSeen") Integer crawlAttemptLastSeen,
    @Nullable @JsonProperty("latestCrawlAttemptForDataset") Integer latestCrawlAttemptForDataset) {

    checkArgument(occurrenceKey > 0, "occurrenceKey must be greater than 0");
    this.occurrenceKey = occurrenceKey;
    this.deletionReason = checkNotNull(deletionReason, "deletionReason can't be null");
    if (deletionReason == OccurrenceDeletionReason.NOT_SEEN_IN_LAST_CRAWL) {
      checkArgument(crawlAttemptLastSeen != null && crawlAttemptLastSeen > 0,
        "crawlAttemptLastSeen must be greater than 0");
      checkArgument(latestCrawlAttemptForDataset != null && latestCrawlAttemptForDataset > 0,
        "latestCrawlAttemptForDataset must be greater than 0");
    }
    this.crawlAttemptLastSeen = crawlAttemptLastSeen;
    this.latestCrawlAttemptForDataset = latestCrawlAttemptForDataset;
  }

  @Override
  public String getRoutingKey() {
    return "occurrence.delete.occurrence";
  }

  public long getOccurrenceKey() {
    return occurrenceKey;
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
    return Objects.hashCode(occurrenceKey, deletionReason, crawlAttemptLastSeen, latestCrawlAttemptForDataset);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final DeleteOccurrenceMessage other = (DeleteOccurrenceMessage) obj;
    return Objects.equal(this.occurrenceKey, other.occurrenceKey) && Objects
      .equal(this.deletionReason, other.deletionReason) && Objects
             .equal(this.crawlAttemptLastSeen, other.crawlAttemptLastSeen) && Objects
             .equal(this.latestCrawlAttemptForDataset, other.latestCrawlAttemptForDataset);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("occurrenceKey", occurrenceKey).add("deletionReason", deletionReason)
      .add("crawlAttemptLastSeen", crawlAttemptLastSeen)
      .add("latestCrawlAttemptForDataset", latestCrawlAttemptForDataset).toString();
  }
}
