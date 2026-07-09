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
import org.gbif.common.messaging.ExchangeType;
import org.gbif.common.messaging.MessageBinding;
import org.gbif.common.messaging.util.MessageUtils;

import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonSerialize
@EqualsAndHashCode(callSuper = false)
@MessageBinding(
  exchange = ExchangeType.OCCURRENCE,
  routingKey = DwcDpStageMessage.ROUTING_KEY)
public class DwcDpStageMessage implements PipelineBasedMessage {

  public static final String ROUTING_KEY = "occurrence.dwcdp.stage";

  private UUID datasetUuid;
  private Integer attempt;
  private Set<String> pipelineSteps;
  private Long executionId;
  private boolean containsOccurrences;
  private boolean containsEvents;

  @JsonCreator
  public DwcDpStageMessage(
    @JsonProperty("datasetUuid") UUID datasetUuid,
    @JsonProperty("attempt") Integer attempt,
    @JsonProperty("pipelineSteps") Set<String> pipelineSteps,
    @JsonProperty("executionId") Long executionId,
    @JsonProperty("containsOccurrences") boolean containsOccurrences,
    @JsonProperty("containsEvents") boolean containsEvents) {
    this.datasetUuid = datasetUuid;
    this.attempt = attempt;
    this.pipelineSteps = pipelineSteps;
    this.executionId = executionId;
    this.containsOccurrences = containsOccurrences;
    this.containsEvents = containsEvents;
  }

  @Override
  public DatasetInfo getDatasetInfo() {
    return new DatasetInfo(getDataSetType(), containsOccurrences, containsEvents);
  }

  private DatasetType getDataSetType() {
    if (containsEvents) {
      return DatasetType.SAMPLING_EVENT;
    }
    if (containsOccurrences) {
      return DatasetType.OCCURRENCE;
    }
    return DatasetType.METADATA;
  }

  @Override
  public String toString() {
    return MessageUtils.toString(this);
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }
}
