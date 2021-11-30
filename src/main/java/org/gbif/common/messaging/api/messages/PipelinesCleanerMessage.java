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
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/** This message is used trigger validator cleaner. */
public class PipelinesCleanerMessage implements DatasetBasedMessage {

  public static final String ROUTING_KEY = "occurrence.pipelines.cleaner";

  private UUID datasetUuid;
  private int attempt;
  private boolean isValidator = false;

  public PipelinesCleanerMessage() {}

  @JsonCreator
  public PipelinesCleanerMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("attempt") int attempt,
      @JsonProperty("isValidator") Boolean isValidator) {
    this.datasetUuid = Objects.requireNonNull(datasetUuid, "datasetUuid can't be null");
    PreconditionUtils.checkArgument(attempt >= 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    if (isValidator != null) {
      this.isValidator = isValidator;
    }
  }

  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  public Integer getAttempt() {
    return attempt;
  }

  @Override
  public String getRoutingKey() {
    String key = ROUTING_KEY;
    if (isValidator) {
      key = key + "." + "validator";
    }
    return key;
  }

  public boolean isValidator() {
    return isValidator;
  }

  public PipelinesCleanerMessage setDatasetUuid(UUID datasetUuid) {
    this.datasetUuid = datasetUuid;
    return this;
  }

  public PipelinesCleanerMessage setAttempt(int attempt) {
    this.attempt = attempt;
    return this;
  }

  public PipelinesCleanerMessage setValidator(boolean validator) {
    isValidator = validator;
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
    PipelinesCleanerMessage that = (PipelinesCleanerMessage) o;
    return attempt == that.attempt
        && Objects.equals(datasetUuid, that.datasetUuid)
        && isValidator == that.isValidator;
  }

  @Override
  public int hashCode() {
    return Objects.hash(datasetUuid, attempt, isValidator);
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
