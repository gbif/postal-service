/*
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

import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This message instructs the occurrence deletion service to delete all occurrence records for the
 * given dataset.
 */
public class DeleteDatasetOccurrencesMessage implements DatasetBasedMessage {

  private final UUID datasetUuid;
  private final OccurrenceDeletionReason deletionReason;

  @JsonCreator
  public DeleteDatasetOccurrencesMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("deletionReason") OccurrenceDeletionReason deletionReason) {
    this.datasetUuid = Objects.requireNonNull(datasetUuid, "datasetUuid can't be null");
    this.deletionReason = Objects.requireNonNull(deletionReason, "deletionReason can't be null");
  }

  @Override
  public String getRoutingKey() {
    return "occurrence.delete.dataset";
  }

  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  public OccurrenceDeletionReason getDeletionReason() {
    return deletionReason;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DeleteDatasetOccurrencesMessage that = (DeleteDatasetOccurrencesMessage) o;
    return Objects.equals(datasetUuid, that.datasetUuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(datasetUuid);
  }
}
