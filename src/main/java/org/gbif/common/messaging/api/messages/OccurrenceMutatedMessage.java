/*
 * Copyright 2020 Global Biodiversity Information Facility (GBIF)
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

import org.gbif.api.model.occurrence.Occurrence;
import org.gbif.api.vocabulary.OccurrencePersistenceStatus;
import org.gbif.utils.PreconditionUtils;

import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The message sent whenever an "interpreted" occurrence has changed (either NEW, UPDATED, or
 * DELETED). An interpreted occurrence is the result of interpreting the values of a verbatim
 * occurrence.
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
  public OccurrenceMutatedMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("status") OccurrencePersistenceStatus status,
      @Nullable @JsonProperty("crawlAttempt") Integer crawlAttempt,
      @Nullable @JsonProperty("oldOccurrence") Occurrence oldOccurrence,
      @Nullable @JsonProperty("newOccurrence") Occurrence newOccurrence,
      @Nullable @JsonProperty("deletionReason") OccurrenceDeletionReason deletionReason,
      @Nullable @JsonProperty("crawlAttemptLastSeen") Integer crawlAttemptLastSeen,
      @Nullable @JsonProperty("latestCrawlAttemptForDataset")
          Integer latestCrawlAttemptForDataset) {
    this.datasetUuid = Objects.requireNonNull(datasetUuid, "datasetUuid can't be null");
    this.status = Objects.requireNonNull(status, "status can't be null");
    switch (status) {
      case NEW:
        Objects.requireNonNull(newOccurrence, "newOccurrence can't be null");
        PreconditionUtils.checkArgument(crawlAttempt != null && crawlAttempt > 0, "attempt must be greater than 0");
        break;
      case UPDATED:
        Objects.requireNonNull(newOccurrence, "newOccurrence can't be null for updates");
        Objects.requireNonNull(oldOccurrence, "oldOccurrence can't be null for updates");
        PreconditionUtils.checkArgument(crawlAttempt != null && crawlAttempt > 0, "attempt must be greater than 0");
        break;
      case DELETED:
        Objects.requireNonNull(oldOccurrence, "oldOccurrence can't be null for deletes");
        Objects.requireNonNull(deletionReason, "deletionReason can't be null for deletes");
        if (deletionReason == OccurrenceDeletionReason.NOT_SEEN_IN_LAST_CRAWL) {
          PreconditionUtils.checkArgument(
              crawlAttemptLastSeen != null && crawlAttemptLastSeen > 0,
              "crawlAttemptLastSeen must be greater than 0");
          PreconditionUtils.checkArgument(
              latestCrawlAttemptForDataset != null && latestCrawlAttemptForDataset > 0,
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

  public static OccurrenceMutatedMessage buildNewMessage(
      UUID datasetUuid, Occurrence newOccurrence, int crawlAttempt) {
    return new OccurrenceMutatedMessage(
        datasetUuid,
        OccurrencePersistenceStatus.NEW,
        crawlAttempt,
        null,
        newOccurrence,
        null,
        null,
        null);
  }

  public static OccurrenceMutatedMessage buildUpdateMessage(
      UUID datasetUuid, Occurrence oldOccurrence, Occurrence newOccurrence, int crawlAttempt) {
    return new OccurrenceMutatedMessage(
        datasetUuid,
        OccurrencePersistenceStatus.UPDATED,
        crawlAttempt,
        oldOccurrence,
        newOccurrence,
        null,
        null,
        null);
  }

  public static OccurrenceMutatedMessage buildDeleteMessage(
      UUID datasetUuid,
      Occurrence oldOccurrence,
      OccurrenceDeletionReason reason,
      Integer crawlAttemptLastSeen,
      Integer lastCrawlAttemptForDataset) {
    return new OccurrenceMutatedMessage(
        datasetUuid,
        OccurrencePersistenceStatus.DELETED,
        null,
        oldOccurrence,
        null,
        reason,
        crawlAttemptLastSeen,
        lastCrawlAttemptForDataset);
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OccurrenceMutatedMessage that = (OccurrenceMutatedMessage) o;
    return Objects.equals(datasetUuid, that.datasetUuid)
        && Objects.equals(crawlAttempt, that.crawlAttempt)
        && status == that.status
        && Objects.equals(oldOccurrence, that.oldOccurrence)
        && Objects.equals(newOccurrence, that.newOccurrence)
        && deletionReason == that.deletionReason
        && Objects.equals(crawlAttemptLastSeen, that.crawlAttemptLastSeen)
        && Objects.equals(latestCrawlAttemptForDataset, that.latestCrawlAttemptForDataset);
  }

  @Override
  public int hashCode() {
    return Objects.hash(datasetUuid, crawlAttempt, status, oldOccurrence, newOccurrence, deletionReason,
        crawlAttemptLastSeen, latestCrawlAttemptForDataset);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", OccurrenceMutatedMessage.class.getSimpleName() + "[", "]")
        .add("datasetUuid=" + datasetUuid)
        .add("crawlAttempt=" + crawlAttempt)
        .add("status=" + status)
        .add("oldOccurrence=" + oldOccurrence)
        .add("newOccurrence=" + newOccurrence)
        .add("deletionReason=" + deletionReason)
        .add("crawlAttemptLastSeen=" + crawlAttemptLastSeen)
        .add("latestCrawlAttemptForDataset=" + latestCrawlAttemptForDataset)
        .toString();
  }
}
