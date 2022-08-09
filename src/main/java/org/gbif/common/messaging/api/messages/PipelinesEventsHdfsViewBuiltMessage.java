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

import org.gbif.utils.PreconditionUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/** This message indicates that the Event HDFS view of a dataset has finished. */
public class PipelinesEventsHdfsViewBuiltMessage implements PipelineBasedMessage {

  public static final String ROUTING_KEY = "occurrence.pipelines.events.hdfsview.finished";

  private UUID datasetUuid;
  private int attempt;
  private Set<String> pipelineSteps;
  private String runner;
  private Long executionId;

  public PipelinesEventsHdfsViewBuiltMessage() {}

  @JsonCreator
  public PipelinesEventsHdfsViewBuiltMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("attempt") int attempt,
      @JsonProperty("pipelineSteps") Set<String> pipelineSteps,
      @JsonProperty("runner") String runner,
      @JsonProperty("executionId") Long executionId) {
    this.datasetUuid = Objects.requireNonNull(datasetUuid, "datasetUuid can't be null");
    PreconditionUtils.checkArgument(attempt >= 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    this.pipelineSteps = pipelineSteps == null ? Collections.emptySet() : pipelineSteps;
    this.runner = runner;
    this.executionId = executionId;
  }

  public PipelinesEventsHdfsViewBuiltMessage(
      UUID datasetUuid, int attempt, Set<String> pipelineSteps) {
    this(datasetUuid, attempt, pipelineSteps, null, null);
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
    if (runner != null && !runner.isEmpty()) {
      return key + "." + runner.toLowerCase();
    }
    return key;
  }

  public String getRunner() {
    return runner;
  }

  public PipelinesEventsHdfsViewBuiltMessage setDatasetUuid(UUID datasetUuid) {
    this.datasetUuid = datasetUuid;
    return this;
  }

  public PipelinesEventsHdfsViewBuiltMessage setAttempt(int attempt) {
    this.attempt = attempt;
    return this;
  }

  public PipelinesEventsHdfsViewBuiltMessage setPipelineSteps(Set<String> pipelineSteps) {
    this.pipelineSteps = pipelineSteps;
    return this;
  }

  public PipelinesEventsHdfsViewBuiltMessage setRunner(String runner) {
    this.runner = runner;
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
    PipelinesEventsHdfsViewBuiltMessage that = (PipelinesEventsHdfsViewBuiltMessage) o;
    return attempt == that.attempt
        && Objects.equals(datasetUuid, that.datasetUuid)
        && Objects.equals(pipelineSteps, that.pipelineSteps)
        && Objects.equals(runner, that.runner)
        && Objects.equals(executionId, that.executionId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(datasetUuid, attempt, pipelineSteps, runner, executionId);
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
