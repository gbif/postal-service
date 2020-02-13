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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

/**
 * This message instructs the dataset mutator service to send InterpretVerbatimMessages for each
 * occurrence in the dataset.
 */
public class InterpretDatasetMessage implements DatasetBasedMessage {

  private final UUID datasetUuid;

  @JsonCreator
  public InterpretDatasetMessage(@JsonProperty("datasetUuid") UUID datasetUuid) {
    this.datasetUuid = datasetUuid;
  }

  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  @Override
  public String getRoutingKey() {
    return "occurrence.interpret.dataset";
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(datasetUuid);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final InterpretDatasetMessage other = (InterpretDatasetMessage) obj;
    return Objects.equal(this.datasetUuid, other.datasetUuid);
  }
}
