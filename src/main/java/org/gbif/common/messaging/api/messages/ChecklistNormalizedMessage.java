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

import java.util.UUID;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/** The message sent whenever an entire checklist is imported into neo and normalized. */
public class ChecklistNormalizedMessage implements DatasetBasedMessage {
  public static final String ROUTING_KEY = "checklist.normalized";

  private final UUID datasetUuid;

  @JsonCreator
  public ChecklistNormalizedMessage(@JsonProperty("datasetUuid") UUID datasetUuid) {
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

  @Override
  public int hashCode() {
    return Objects.hashCode(datasetUuid);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ChecklistNormalizedMessage other = (ChecklistNormalizedMessage) obj;
    return Objects.equal(this.datasetUuid, other.datasetUuid);
  }
}
