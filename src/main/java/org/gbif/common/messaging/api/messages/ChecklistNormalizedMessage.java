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

import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The message sent whenever an entire checklist is imported into neo and normalized. */
public class ChecklistNormalizedMessage implements DatasetBasedMessage {
  public static final String ROUTING_KEY = "checklist.normalized";

  private final UUID datasetUuid;

  @JsonCreator
  public ChecklistNormalizedMessage(@JsonProperty("datasetUuid") UUID datasetUuid) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChecklistNormalizedMessage that = (ChecklistNormalizedMessage) o;
    return Objects.equals(datasetUuid, that.datasetUuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(datasetUuid);
  }
}
