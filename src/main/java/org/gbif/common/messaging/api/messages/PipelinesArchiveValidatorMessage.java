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

import org.gbif.api.util.PreconditionUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This message is used trigger Checklists validations.
 */
public class PipelinesArchiveValidatorMessage implements PipelineBasedMessage {

  public static final String ROUTING_KEY = "occurrence.pipelines.archive.validator";

  private UUID datasetUuid;
  private int attempt;
  private Set<String> pipelineSteps;
  private Long executionId;
  private String fileFormat;
  private boolean isValidator = false;

  public PipelinesArchiveValidatorMessage() {}

  @JsonCreator
  public PipelinesArchiveValidatorMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("attempt") int attempt,
      @JsonProperty("pipelineSteps") Set<String> pipelineSteps,
      @JsonProperty("executionId") Long executionId,
      @JsonProperty("isValidator") Boolean isValidator,
      @JsonProperty("fileFormat") String fileFormat) {
    this.datasetUuid = Objects.requireNonNull(datasetUuid, "datasetUuid can't be null");
    PreconditionUtils.checkArgument(attempt >= 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    this.pipelineSteps = pipelineSteps == null ? Collections.emptySet() : pipelineSteps;
    this.executionId = executionId;
    this.fileFormat = fileFormat;
    if (isValidator != null) {
      this.isValidator = isValidator;
    }
  }

  @Override
  public void setExecutionId(Long executionId) {
    this.executionId = executionId;
  }

  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
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
  public String getRoutingKey() {
    String key = ROUTING_KEY;
    if (isValidator) {
      key = key + "." + "validator";
    }
    return key;
  }

  public String getFileFormat() {
    return fileFormat;
  }

  public boolean isValidator() {
    return isValidator;
  }

  public PipelinesArchiveValidatorMessage setDatasetUuid(UUID datasetUuid) {
    this.datasetUuid = datasetUuid;
    return this;
  }

  public PipelinesArchiveValidatorMessage setAttempt(int attempt) {
    this.attempt = attempt;
    return this;
  }

  public PipelinesArchiveValidatorMessage setPipelineSteps(Set<String> pipelineSteps) {
    this.pipelineSteps = pipelineSteps;
    return this;
  }

  public PipelinesArchiveValidatorMessage setValidator(boolean validator) {
    isValidator = validator;
    return this;
  }

  public PipelinesArchiveValidatorMessage setFileFormat(String fileFormat) {
    this.fileFormat = fileFormat;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PipelinesArchiveValidatorMessage that = (PipelinesArchiveValidatorMessage) o;
    return attempt == that.attempt
        && Objects.equals(datasetUuid, that.datasetUuid)
        && Objects.equals(pipelineSteps, that.pipelineSteps)
        && Objects.equals(fileFormat, that.fileFormat)
        && Objects.equals(executionId, that.executionId)
        && isValidator == that.isValidator;
  }

  @Override
  public int hashCode() {
    return Objects.hash(datasetUuid, attempt, pipelineSteps, fileFormat, executionId, isValidator);
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
