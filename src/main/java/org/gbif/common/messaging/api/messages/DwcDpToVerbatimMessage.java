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

/**
 * Message triggering the DWCDP_TO_VERBATIM step — conversion of DwC-DP Parquet files on HDFS
 * into a verbatim.avro file ready for the existing identifier and interpretation pipeline.
 *
 * <p>Routing key suffix ({@code .standalone} / {@code .distributed}) is appended by the sender
 * so that the appropriate consumer queue binding picks it up without a balancer round-trip.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonSerialize
@EqualsAndHashCode(callSuper = false)
@MessageBinding(
  exchange = ExchangeType.OCCURRENCE,
  routingKey = DwcDpToVerbatimMessage.ROUTING_KEY_BASE)
public class DwcDpToVerbatimMessage implements PipelineBasedMessage {

  public static final String ROUTING_KEY_BASE = "occurrence.dwcdp.to-verbatim";
  public static final String ROUTING_KEY_STANDALONE = ROUTING_KEY_BASE + ".standalone";
  public static final String ROUTING_KEY_DISTRIBUTED = ROUTING_KEY_BASE + ".distributed";

  private UUID datasetUuid;
  private Integer attempt;
  private Set<String> pipelineSteps;
  private Long executionId;
  private boolean containsOccurrences;
  private boolean containsEvents;
  private boolean standalone;

  @JsonCreator
  public DwcDpToVerbatimMessage(
    @JsonProperty("datasetUuid") UUID datasetUuid,
    @JsonProperty("attempt") Integer attempt,
    @JsonProperty("pipelineSteps") Set<String> pipelineSteps,
    @JsonProperty("executionId") Long executionId,
    @JsonProperty("containsOccurrences") boolean containsOccurrences,
    @JsonProperty("containsEvents") boolean containsEvents,
    @JsonProperty("standalone") boolean standalone) {
    this.datasetUuid = datasetUuid;
    this.attempt = attempt;
    this.pipelineSteps = pipelineSteps;
    this.executionId = executionId;
    this.containsOccurrences = containsOccurrences;
    this.containsEvents = containsEvents;
    this.standalone = standalone;
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

  /**
   * Returns the specific routing key for this message based on whether it targets the standalone
   * or distributed consumer. The binding on each queue uses the suffixed key, so the right
   * callback picks it up without a balancer round-trip.
   */
  @Override
  public String getRoutingKey() {
    return standalone ? ROUTING_KEY_STANDALONE : ROUTING_KEY_DISTRIBUTED;
  }
}
