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

import org.gbif.api.vocabulary.OccurrencePersistenceStatus;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The message sent whenever a "verbatim" occurrence is persisted. A verbatim occurrence is the
 * result of parsing incoming fragments, but has had no interpretation (e.g. dates, co-ordinates,
 * etc) applied. Note that this message will only have status NEW or UPDATED - never UNCHANGED.
 */
public class VerbatimPersistedMessage implements DatasetBasedMessage {

  private final UUID datasetUuid;

  private final int attempt;

  private final OccurrencePersistenceStatus status;

  private final long occurrenceKey;

  @JsonCreator
  public VerbatimPersistedMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("attempt") int attempt,
      @JsonProperty("status") OccurrencePersistenceStatus status,
      @JsonProperty("occurrenceKey") long occurrenceKey) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    checkArgument(attempt > 0, "attempt must be greater than 0");
    this.attempt = attempt;
    this.status = checkNotNull(status, "status can't be null");
    this.occurrenceKey = occurrenceKey;
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
    final VerbatimPersistedMessage other = (VerbatimPersistedMessage) obj;
    return Objects.equal(this.datasetUuid, other.datasetUuid)
        && Objects.equal(this.attempt, other.attempt)
        && Objects.equal(this.status, other.status)
        && Objects.equal(this.occurrenceKey, other.occurrenceKey);
  }
}
