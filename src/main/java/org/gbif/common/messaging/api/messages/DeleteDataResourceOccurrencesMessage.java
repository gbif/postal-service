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

import org.gbif.common.messaging.api.Message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

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
  public int hashCode() {
    return Objects.hashCode(dataResourceId);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final DeleteDataResourceOccurrencesMessage other = (DeleteDataResourceOccurrencesMessage) obj;
    return Objects.equal(this.dataResourceId, other.dataResourceId);
  }
}
