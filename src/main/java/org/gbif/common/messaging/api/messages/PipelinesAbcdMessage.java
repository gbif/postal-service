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

import org.gbif.api.vocabulary.EndpointType;
import org.gbif.utils.PreconditionUtils;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.gbif.api.model.pipelines.StepType.VALIDATOR_ABCD_TO_VERBATIM;

/**
 * We send this every time an ABCD archive has been downloaded. This includes cases when the archive
 * hasn't been modified since we last downloaded it.
 */
public class PipelinesAbcdMessage implements PipelineBasedMessage {

  public static final String ROUTING_KEY = AbcdaDownloadFinishedMessage.ROUTING_KEY;

  private UUID datasetUuid;
  private URI source;
  private int attempt;
  private boolean modified;
  private Set<String> pipelineSteps;
  private EndpointType endpointType;
  private Long executionId;

  @JsonCreator
  public PipelinesAbcdMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("source") URI source,
      @JsonProperty("attempt") int attempt,
      @JsonProperty("modified") boolean modified,
      @JsonProperty("pipelineSteps") Set<String> pipelineSteps,
      @JsonProperty("endpointType") EndpointType endpointType,
      @JsonProperty("executionId") Long executionId) {
    this.datasetUuid = Objects.requireNonNull(datasetUuid, "datasetUuid can't be null");
    this.source = Objects.requireNonNull(source, "source can't be null");
    PreconditionUtils.checkArgument(attempt > 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    this.modified = modified;
    this.pipelineSteps = pipelineSteps == null ? Collections.emptySet() : pipelineSteps;
    this.endpointType = endpointType;
    this.executionId = executionId;
  }

  /** @return dataset uuid */
  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
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

  /**
   * @return true if the archive has changed since we last downloaded it or never been downloaded
   *     before
   */
  public boolean isModified() {
    return modified;
  }

  @Override
  public String getRoutingKey() {
    String key = ROUTING_KEY;
    if (pipelineSteps != null && pipelineSteps.contains(VALIDATOR_ABCD_TO_VERBATIM.name())) {
      key = key + ".validator";
    }
    return key;
  }

  public PipelinesAbcdMessage setDatasetUuid(UUID datasetUuid) {
    this.datasetUuid = datasetUuid;
    return this;
  }

  public PipelinesAbcdMessage setSource(URI source) {
    this.source = source;
    return this;
  }

  public PipelinesAbcdMessage setAttempt(int attempt) {
    this.attempt = attempt;
    return this;
  }

  public PipelinesAbcdMessage setModified(boolean modified) {
    this.modified = modified;
    return this;
  }

  public PipelinesAbcdMessage setPipelineSteps(Set<String> pipelineSteps) {
    this.pipelineSteps = pipelineSteps;
    return this;
  }

  public PipelinesAbcdMessage setEndpointType(EndpointType endpointType) {
    this.endpointType = endpointType;
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
    PipelinesAbcdMessage that = (PipelinesAbcdMessage) o;
    return attempt == that.attempt
        && modified == that.modified
        && Objects.equals(datasetUuid, that.datasetUuid)
        && Objects.equals(source, that.source)
        && Objects.equals(pipelineSteps, that.pipelineSteps)
        && endpointType == that.endpointType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(datasetUuid, source, attempt, modified, pipelineSteps, endpointType);
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
