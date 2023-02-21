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

import org.gbif.api.vocabulary.DatasetType;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface PipelineBasedMessage extends DatasetBasedMessage {

  Integer getAttempt();

  Set<String> getPipelineSteps();

  Long getExecutionId();

  void setExecutionId(Long executionId);

  @JsonIgnore
  DatasetInfo getDatasetInfo();

  class DatasetInfo {

    DatasetType datasetType;
    boolean containsOccurrences;
    boolean containsEvents;

    public DatasetInfo(
        DatasetType datasetType, boolean containsOccurrences, boolean containsEvents) {
      this.datasetType = datasetType;
      this.containsOccurrences = containsOccurrences;
      this.containsEvents = containsEvents;
    }

    public DatasetType getDatasetType() {
      return datasetType;
    }

    public boolean isContainsOccurrences() {
      return containsOccurrences;
    }

    public boolean isContainsEvents() {
      return containsEvents;
    }
  }
}
