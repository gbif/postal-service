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

import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This message instructs the dataset mutator service to send PipelinesArchiveValidatorMessage for
 * each occurrence in the dataset.
 */
public class PipelinesChecklistValidatorMessage extends PipelinesArchiveValidatorMessage implements RpcMessage{

  public static final String ROUTING_KEY = "occurrence.pipelines.checklist.validator";

  private String correlationId;

  private String replyTo;


  public PipelinesChecklistValidatorMessage() {}

  @JsonCreator
  public PipelinesChecklistValidatorMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("attempt") int attempt,
      @JsonProperty("pipelineSteps") Set<String> pipelineSteps,
      @JsonProperty("executionId") Long executionId,
      @JsonProperty("fileFormat") String fileFormat) {
    super(datasetUuid, attempt, pipelineSteps, executionId, true, fileFormat);
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }

  @Override
  public void setCorrelationId(String correlationId) {
    this.correlationId = correlationId;
  }

  @Override
  public String getCorrelationId() {
    return correlationId;
  }

  @Override
  public void setReplyTo(String replyTo) {
    this.replyTo = replyTo;
  }

  @Override
  public String getReplyTo() {
    return replyTo;
  }
}
