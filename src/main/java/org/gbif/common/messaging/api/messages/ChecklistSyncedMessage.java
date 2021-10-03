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

import org.gbif.utils.PreconditionUtils;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    this.crawlFinished = Objects.requireNonNull(crawlFinished, "crawlFinished date missing");
    PreconditionUtils.checkArgument(recordsSynced >= 0);
    this.recordsSynced = recordsSynced;
    PreconditionUtils.checkArgument(recordsDeleted >= 0);
    this.recordsDeleted = recordsDeleted;
    this.datasetUuid = Objects.requireNonNull(datasetUuid, "datasetUuid can't be null");
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChecklistSyncedMessage that = (ChecklistSyncedMessage) o;
    return recordsSynced == that.recordsSynced
        && recordsDeleted == that.recordsDeleted
        && Objects.equals(datasetUuid, that.datasetUuid)
        && Objects.equals(crawlFinished, that.crawlFinished);
  }

  @Override
  public int hashCode() {
    return Objects.hash(datasetUuid, crawlFinished, recordsSynced, recordsDeleted);
  }
}
