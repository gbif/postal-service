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

import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    checkArgument(attempt > 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    checkArgument(totalRecordCount >= 0, "totalRecordCount has to be greater than or equal to 0");
    this.totalRecordCount = totalRecordCount;
    this.reason = checkNotNull(reason, "reason can't be null");
    this.endpointType = endpointType;
    this.platform = Optional.ofNullable(platform).orElse(Platform.ALL);
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
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof CrawlFinishedMessage)) {
      return false;
    }

    final CrawlFinishedMessage other = (CrawlFinishedMessage) obj;
    return Objects.equal(this.datasetUuid, other.datasetUuid)
        && Objects.equal(this.attempt, other.attempt)
        && Objects.equal(this.totalRecordCount, other.totalRecordCount)
        && Objects.equal(this.reason, other.reason)
        && Objects.equal(this.endpointType, other.endpointType)
        && Objects.equal(this.platform, other.platform);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(datasetUuid, attempt, totalRecordCount, reason, endpointType, platform);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("datasetUuid", datasetUuid)
        .add("attempt", attempt)
        .add("totalRecordCount", totalRecordCount)
        .add("reason", reason)
        .add("endpointType", endpointType)
        .add("platform", platform)
        .toString();
  }
}
