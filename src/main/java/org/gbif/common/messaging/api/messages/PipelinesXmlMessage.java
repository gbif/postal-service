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

import org.gbif.api.model.crawler.FinishReason;
import org.gbif.api.vocabulary.EndpointType;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/** We send this every time we finish a crawl. */
public class PipelinesXmlMessage implements PipelineBasedMessage {

  public static final String ROUTING_KEY = CrawlFinishedMessage.ROUTING_KEY;

  private UUID datasetUuid;
  private int attempt;
  private int totalRecordCount;
  private FinishReason reason;
  private Set<String> pipelineSteps;
  private EndpointType endpointType;
  private Platform platform;
  private Long executionId;
  private boolean isValidator = false;

  public PipelinesXmlMessage() {}

  @JsonCreator
  public PipelinesXmlMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("attempt") int attempt,
      @JsonProperty("totalRecordCount") int totalRecordCount,
      @JsonProperty("reason") FinishReason reason,
      @JsonProperty("pipelineSteps") Set<String> pipelineSteps,
      @JsonProperty("endpointType") EndpointType endpointType,
      @JsonProperty("platform") Platform platform,
      @JsonProperty("executionId") Long executionId,
      @JsonProperty("isValidator") Boolean isValidator) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    checkArgument(attempt > 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    this.totalRecordCount = totalRecordCount;
    this.reason = checkNotNull(reason, "reason can't be null");
    this.pipelineSteps = pipelineSteps == null ? Collections.emptySet() : pipelineSteps;
    this.endpointType = endpointType;
    this.platform = Optional.ofNullable(platform).orElse(Platform.ALL);
    this.executionId = executionId;
    if (isValidator != null) {
      this.isValidator = isValidator;
    }
  }

  @Override
  public Integer getAttempt() {
    return attempt;
  }

  @Override
  public Set<String> getPipelineSteps() {
    return pipelineSteps;
  }

  @Override
  public Long getExecutionId() {
    return executionId;
  }

  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  public FinishReason getReason() {
    return reason;
  }

  public int getTotalRecordCount() {
    return totalRecordCount;
  }

  public EndpointType getEndpointType() {
    return endpointType;
  }

  public Platform getPlatform() {
    return platform;
  }

  @Override
  public String getRoutingKey() {
    String key = ROUTING_KEY;
    if (isValidator) {
      key = key + "." + "validator";
    }
    return key;
  }

  public PipelinesXmlMessage setDatasetUuid(UUID datasetUuid) {
    this.datasetUuid = datasetUuid;
    return this;
  }

  public PipelinesXmlMessage setAttempt(int attempt) {
    this.attempt = attempt;
    return this;
  }

  public PipelinesXmlMessage setTotalRecordCount(int totalRecordCount) {
    this.totalRecordCount = totalRecordCount;
    return this;
  }

  public PipelinesXmlMessage setReason(FinishReason reason) {
    this.reason = reason;
    return this;
  }

  public PipelinesXmlMessage setPipelineSteps(Set<String> pipelineSteps) {
    this.pipelineSteps = pipelineSteps;
    return this;
  }

  public PipelinesXmlMessage setEndpointType(EndpointType endpointType) {
    this.endpointType = endpointType;
    return this;
  }

  public PipelinesXmlMessage setPlatform(Platform platform) {
    this.platform = platform;
    return this;
  }

  public boolean isValidator() {
    return isValidator;
  }

  public PipelinesXmlMessage setValidator(boolean validator) {
    isValidator = validator;
    return this;
  }

  @Override
  public void setExecutionId(Long executionId) {
    this.executionId = executionId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PipelinesXmlMessage that = (PipelinesXmlMessage) o;
    return attempt == that.attempt
        && totalRecordCount == that.totalRecordCount
        && Objects.equals(datasetUuid, that.datasetUuid)
        && reason == that.reason
        && Objects.equals(pipelineSteps, that.pipelineSteps)
        && Objects.equals(endpointType, that.endpointType)
        && Objects.equals(executionId, that.executionId)
        && isValidator == that.isValidator;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        datasetUuid,
        attempt,
        totalRecordCount,
        reason,
        pipelineSteps,
        endpointType,
        executionId,
        isValidator);
  }

  @Override
  public String toString() {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.writeValueAsString(this);
    } catch (IOException e) {
      // NOP
    }
    return "";
  }
}
