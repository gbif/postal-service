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

import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonSerialize
@MessageBinding(
  exchange = ExchangeType.OCCURRENCE,
  routingKey = DwcDpNfsToHdfsMessage.ROUTING_KEY)
public class DwcDpNfsToHdfsMessage implements PipelineBasedMessage {

  public static final String ROUTING_KEY = "occurrence.dwcdp.nfs-to-hdfs";

  private final UUID datasetUuid;
  private final Integer attempt;
  private Set<String> pipelineSteps;
  private Long executionId;

  @Override
  public DatasetInfo getDatasetInfo() {
    return new DatasetInfo(DatasetType.OCCURRENCE, true, false);
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }
}
