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

import org.gbif.common.messaging.api.Message;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This message instructs the occurrence deletion service to delete all occurrence records for the
 * given data resource id (the legacy id from the MySQL portal).
 */
public class DeleteDataResourceOccurrencesMessage implements Message {

  private final int dataResourceId;

  @JsonCreator
  public DeleteDataResourceOccurrencesMessage(@JsonProperty("dataResourceId") int dataResourceId) {
    this.dataResourceId = dataResourceId;
  }

  @Override
  public String getRoutingKey() {
    return "occurrence.delete.dataresource";
  }

  public int getDataResourceId() {
    return dataResourceId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DeleteDataResourceOccurrencesMessage that = (DeleteDataResourceOccurrencesMessage) o;
    return dataResourceId == that.dataResourceId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(dataResourceId);
  }
}
