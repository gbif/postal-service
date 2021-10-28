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

import java.util.UUID;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Message to send to request a new crawl of a dataset.
 *
 * <p>A priority can be given to this request. Lower numbers mean higher priority. Requests with a
 * higher priority will be running first but there is no guarantee on the scheduling algorithm so
 * consider this as a hint.
 */
public class StartCrawlMessage implements DatasetBasedMessage {

  private final UUID datasetUuid;

  private final Integer priority;

  private final Platform platform;

  /**
   * Creates a message without an explicit priority. The crawler coordinator is free to choose a
   * default priority in this case.
   *
   * @param datasetUuid to crawl
   */
  public StartCrawlMessage(UUID datasetUuid) {
    this(datasetUuid, null, Platform.ALL);
  }

  @JsonCreator
  public StartCrawlMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("priority") Integer priority,
      @JsonProperty("platform") Platform platform) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    this.priority = priority;
    this.platform = java.util.Optional.ofNullable(platform).orElse(Platform.ALL);
  }

  public StartCrawlMessage(UUID datasetUuid, int priority) {
    this(datasetUuid, priority, Platform.ALL);
  }

  /**
   * Can be used to create a message using predefined priority constants.
   *
   * @param datasetUuid to crawl
   * @param priority to crawl at, if none is provided a default is used
   */
  public StartCrawlMessage(UUID datasetUuid, @Nullable Priority priority) {
    this(datasetUuid, priority != null ? priority.getPriority() : null, Platform.ALL);
  }

  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  @Nullable
  public Integer getPriority() {
    return priority;
  }

  public Platform getPlatform() {
    return platform;
  }

  @Override
  public String getRoutingKey() {
    return "crawl.start";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof StartCrawlMessage)) {
      return false;
    }

    final StartCrawlMessage other = (StartCrawlMessage) obj;
    return Objects.equal(this.datasetUuid, other.datasetUuid)
        && Objects.equal(this.priority, other.priority);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(datasetUuid, priority);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("datasetUuid", datasetUuid)
        .add("priority", priority)
        .toString();
  }

  /** Some predefined priorities that can be used to construct a message. */
  public enum Priority {
    LOW(10),
    NORMAL(0),
    HIGH(-10),
    CRITICAL(-100);

    private final int priority;

    Priority(int priority) {
      this.priority = priority;
    }

    public int getPriority() {
      return priority;
    }
  }
}
