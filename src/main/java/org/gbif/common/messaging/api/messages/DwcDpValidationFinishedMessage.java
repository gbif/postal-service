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

import org.gbif.common.messaging.ExchangeType;
import org.gbif.common.messaging.MessageBinding;
import org.gbif.dp.analysis.api.DatapackageAnalysisResult;
import org.gbif.dp.service.api.DwcDpValidationFinished;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@MessageBinding(exchange = ExchangeType.CRAWLER, routingKey = DwcDpValidationFinishedMessage.ROUTING_KEY)
public class DwcDpValidationFinishedMessage extends DwcDpValidationFinished implements DatasetBasedMessage {

  public static final String ROUTING_KEY = "crawl.dwcdp.validation.finished";

  @JsonCreator
  public DwcDpValidationFinishedMessage(
    @JsonProperty("datasetUuid") UUID datasetUuid,
    @JsonProperty("attempt") int attempt,
    @JsonProperty("valid") Boolean valid,
    @JsonProperty("validationReport") DatapackageAnalysisResult validationReport) {
    super(datasetUuid, attempt, valid, validationReport);
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }
}
