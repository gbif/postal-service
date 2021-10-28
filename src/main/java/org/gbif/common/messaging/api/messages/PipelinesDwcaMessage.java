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

import org.gbif.api.model.crawler.DwcaValidationReport;
import org.gbif.api.vocabulary.DatasetType;
import org.gbif.api.vocabulary.EndpointType;

import java.io.IOException;
import java.net.URI;
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

/**
 * We send this every time a darwin core archive has been validated after being downloaded and its
 * metadata has been synchronized.
 */
public class PipelinesDwcaMessage implements PipelineBasedMessage {

  public static final String ROUTING_KEY = DwcaValidationFinishedMessage.ROUTING_KEY;

  private UUID datasetUuid;
  private DatasetType datasetType;
  private URI source;
  private int attempt;
  private DwcaValidationReport validationReport;
  private Set<String> pipelineSteps;
  private EndpointType endpointType;
  private Platform platform;
  private Long executionId;
  private boolean isValidator = false;

  public PipelinesDwcaMessage() {}

  @JsonCreator
  public PipelinesDwcaMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("datasetType") DatasetType datasetType,
      @JsonProperty("source") URI source,
      @JsonProperty("attempt") int attempt,
      @JsonProperty("validationReport") DwcaValidationReport validationReport,
      @JsonProperty("pipelineSteps") Set<String> pipelineSteps,
      @JsonProperty("endpointType") EndpointType endpointType,
      @JsonProperty("platform") Platform platform,
      @JsonProperty("executionId") Long executionId,
      @JsonProperty("isValidator") Boolean isValidator) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    this.datasetType = checkNotNull(datasetType, "datasetType can't be null");
    this.source = checkNotNull(source, "source can't be null");
    checkArgument(attempt >= 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    this.validationReport = checkNotNull(validationReport, "validationReport can't be null");
    this.pipelineSteps = pipelineSteps == null ? Collections.emptySet() : pipelineSteps;
    this.endpointType = endpointType;
    this.platform = Optional.ofNullable(platform).orElse(Platform.ALL);
    this.executionId = executionId;
    if (isValidator != null) {
      this.isValidator = isValidator;
    }
  }

  /** @return dataset uuid */
  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  /** @return the dataset type as given in the registry */
  public DatasetType getDatasetType() {
    return datasetType;
  }

  /** @return source the archive has been downloaded from */
  public URI getSource() {
    return source;
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

  public EndpointType getEndpointType() {
    return endpointType;
  }

  public Platform getPlatform() {
    return platform;
  }

  /** @return the validationReport for this archive */
  public DwcaValidationReport getValidationReport() {
    return validationReport;
  }

  @Override
  public String getRoutingKey() {
    String key = ROUTING_KEY;
    if (isValidator) {
      key = key + "." + "validator";
    }
    return key;
  }

  public PipelinesDwcaMessage setDatasetUuid(UUID datasetUuid) {
    this.datasetUuid = datasetUuid;
    return this;
  }

  public PipelinesDwcaMessage setDatasetType(DatasetType datasetType) {
    this.datasetType = datasetType;
    return this;
  }

  public PipelinesDwcaMessage setSource(URI source) {
    this.source = source;
    return this;
  }

  public PipelinesDwcaMessage setAttempt(int attempt) {
    this.attempt = attempt;
    return this;
  }

  public PipelinesDwcaMessage setValidationReport(DwcaValidationReport validationReport) {
    this.validationReport = validationReport;
    return this;
  }

  public PipelinesDwcaMessage setPipelineSteps(Set<String> pipelineSteps) {
    this.pipelineSteps = pipelineSteps;
    return this;
  }

  public PipelinesDwcaMessage setEndpointType(EndpointType endpointType) {
    this.endpointType = endpointType;
    return this;
  }

  public PipelinesDwcaMessage setPlatform(Platform platform) {
    this.platform = platform;
    return this;
  }

  public boolean isValidator() {
    return isValidator;
  }

  public PipelinesDwcaMessage setValidator(boolean validator) {
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
    PipelinesDwcaMessage that = (PipelinesDwcaMessage) o;
    return attempt == that.attempt
        && Objects.equals(datasetUuid, that.datasetUuid)
        && datasetType == that.datasetType
        && Objects.equals(source, that.source)
        && Objects.equals(validationReport, that.validationReport)
        && Objects.equals(pipelineSteps, that.pipelineSteps)
        && Objects.equals(endpointType, that.endpointType)
        && Objects.equals(executionId, that.executionId)
        && isValidator == that.isValidator;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        datasetUuid,
        datasetType,
        source,
        attempt,
        validationReport,
        pipelineSteps,
        endpointType,
        executionId);
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
