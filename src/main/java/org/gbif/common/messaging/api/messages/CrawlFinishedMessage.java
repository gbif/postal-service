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

import org.gbif.api.model.crawler.FinishReason;
import org.gbif.api.vocabulary.EndpointType;
import org.gbif.utils.PreconditionUtils;

import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** We send this every time we finish a crawl. */
public class CrawlFinishedMessage implements DatasetBasedMessage {

  public static final String ROUTING_KEY = "crawl.finished";

  private final UUID datasetUuid;
  private final int attempt;
  private final int totalRecordCount;
  private final FinishReason reason;
  private final EndpointType endpointType;
  private final Platform platform;

  @JsonCreator
  public CrawlFinishedMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("attempt") int attempt,
      @JsonProperty("totalRecordCount") int totalRecordCount,
      @JsonProperty("reason") FinishReason reason,
      @JsonProperty("endpointType") EndpointType endpointType,
      @JsonProperty("platform") Platform platform) {
    this.datasetUuid = Objects.requireNonNull(datasetUuid, "datasetUuid can't be null");
    PreconditionUtils.checkArgument(attempt > 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    PreconditionUtils.checkArgument(totalRecordCount >= 0, "totalRecordCount has to be greater than or equal to 0");
    this.totalRecordCount = totalRecordCount;
    this.reason = Objects.requireNonNull(reason, "reason can't be null");
    this.endpointType = endpointType;
    this.platform = platform != null ? platform : Platform.ALL;
  }

  public int getAttempt() {
    return attempt;
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
    return ROUTING_KEY;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CrawlFinishedMessage that = (CrawlFinishedMessage) o;
    return attempt == that.attempt
        && totalRecordCount == that.totalRecordCount
        && Objects.equals(datasetUuid, that.datasetUuid)
        && reason == that.reason
        && endpointType == that.endpointType
        && platform == that.platform;
  }

  @Override
  public int hashCode() {
    return Objects.hash(datasetUuid, attempt, totalRecordCount, reason, endpointType, platform);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", CrawlFinishedMessage.class.getSimpleName() + "[", "]")
        .add("datasetUuid=" + datasetUuid)
        .add("attempt=" + attempt)
        .add("totalRecordCount=" + totalRecordCount)
        .add("reason=" + reason)
        .add("endpointType=" + endpointType)
        .add("platform=" + platform)
        .toString();
  }
}
