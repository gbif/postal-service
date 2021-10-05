/*
 * Copyright 2021 Global Biodiversity Information Facility (GBIF)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Message;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/** This message instructs the occurrence deletion service to delete a single Occurrence. */
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
      @Nullable @JsonProperty("latestCrawlAttemptForDataset")
          Integer latestCrawlAttemptForDataset) {

    checkArgument(occurrenceKey > 0, "occurrenceKey must be greater than 0");
    this.occurrenceKey = occurrenceKey;
    this.deletionReason = checkNotNull(deletionReason, "deletionReason can't be null");
    if (deletionReason == OccurrenceDeletionReason.NOT_SEEN_IN_LAST_CRAWL) {
      checkArgument(
          crawlAttemptLastSeen != null && crawlAttemptLastSeen > 0,
          "crawlAttemptLastSeen must be greater than 0");
      checkArgument(
          latestCrawlAttemptForDataset != null && latestCrawlAttemptForDataset > 0,
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
    return Objects.hashCode(
        occurrenceKey, deletionReason, crawlAttemptLastSeen, latestCrawlAttemptForDataset);
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
    return Objects.equal(this.occurrenceKey, other.occurrenceKey)
        && Objects.equal(this.deletionReason, other.deletionReason)
        && Objects.equal(this.crawlAttemptLastSeen, other.crawlAttemptLastSeen)
        && Objects.equal(this.latestCrawlAttemptForDataset, other.latestCrawlAttemptForDataset);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("occurrenceKey", occurrenceKey)
        .add("deletionReason", deletionReason)
        .add("crawlAttemptLastSeen", crawlAttemptLastSeen)
        .add("latestCrawlAttemptForDataset", latestCrawlAttemptForDataset)
        .toString();
  }
}
