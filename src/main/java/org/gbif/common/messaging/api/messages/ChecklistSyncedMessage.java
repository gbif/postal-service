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

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/** The message sent whenever an entire checklist is imported into checklistbank. */
public class ChecklistSyncedMessage implements DatasetBasedMessage {
  public static final String ROUTING_KEY = "checklist.imported";

  private final UUID datasetUuid;
  private final Date crawlFinished;
  private final int recordsSynced;
  private final int recordsDeleted;

  @JsonCreator
  public ChecklistSyncedMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("crawlFinished") Date crawlFinished,
      @JsonProperty("recordsSynced") int recordsSynced,
      @JsonProperty("recordsDeleted") int recordsDeleted) {
    this.crawlFinished = checkNotNull(crawlFinished, "crawlFinished date missing");
    checkArgument(recordsSynced >= 0);
    this.recordsSynced = recordsSynced;
    checkArgument(recordsDeleted >= 0);
    this.recordsDeleted = recordsDeleted;
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }

  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  public Date getCrawlFinished() {
    return crawlFinished;
  }

  public int getRecordsSynced() {
    return recordsSynced;
  }

  public int getRecordsDeleted() {
    return recordsDeleted;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(datasetUuid, crawlFinished, recordsSynced, recordsDeleted);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ChecklistSyncedMessage other = (ChecklistSyncedMessage) obj;
    return Objects.equal(this.datasetUuid, other.datasetUuid)
        && Objects.equal(this.crawlFinished, other.crawlFinished)
        && Objects.equal(this.recordsSynced, other.recordsSynced)
        && Objects.equal(this.recordsDeleted, other.recordsDeleted);
  }
}
