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
import org.gbif.common.messaging.api.Message;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

/**
 * This message instructs the dataset mutator service to send PipelinesArchiveValidatorMessage for
 * each occurrence in the dataset.
 */
@MessageBinding(exchange = ExchangeType.OCCURRENCE, routingKey = PipelinesChecklistValidatorMessage.ROUTING_KEY)
@Getter
public class PipelinesChecklistValidatorMessage implements Message {

  public static final String ROUTING_KEY = "occurrence.pipelines.checklist.validator";

  private UUID validationKey;
  private String clBResponsePayload;

  public PipelinesChecklistValidatorMessage() {
  }

  @JsonCreator
  public PipelinesChecklistValidatorMessage(
    @JsonProperty("validationKey") UUID validationKey,
    @JsonProperty("clBResponsePayload") String clBResponsePayload) {
    this.validationKey = validationKey;
    this.clBResponsePayload = clBResponsePayload;
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }

}
