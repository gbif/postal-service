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

import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** We send this message every time we get a response from an endpoint. */
public class CrawlResponseMessage implements DatasetBasedMessage {

  private final UUID datasetUuid;

  private final int attempt;

  private final int requestTry;

  private final byte[] response;

  private final long duration;

  private final Integer recordCount;

  private final String status;

  private final Platform platform;

  @JsonCreator
  public CrawlResponseMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("attempt") int attempt,
      @JsonProperty("requestTry") int requestTry,
      @JsonProperty("response") byte[] response,
      @JsonProperty("duration") long duration,
      @Nullable @JsonProperty("recordCount") Integer recordCount,
      @JsonProperty("status") String status,
      @JsonProperty("platform") Platform platform) {
    this.datasetUuid = Objects.requireNonNull(datasetUuid);

    PreconditionUtils.checkArgument(attempt > 0, "attempt has to be greater than 0");
    this.attempt = attempt;

    PreconditionUtils.checkArgument(requestTry > 0, "requestTry has to be greater than 0");
    this.requestTry = requestTry;

    this.response = Objects.requireNonNull(response, "response can't be null");

    PreconditionUtils.checkArgument(duration > 0, "duration has to be greater than 0");
    this.duration = duration;

    PreconditionUtils.checkArgument(
        recordCount == null || recordCount >= 0,
        "recordCount has to be absent or greater than or equal to 0");
    this.recordCount = recordCount;

    this.status = Objects.requireNonNull(status, "status can't be null");

    this.platform = platform != null ? platform : Platform.ALL;
  }

  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  public int getAttempt() {
    return attempt;
  }

  public long getDuration() {
    return duration;
  }

  @Nullable
  public Integer getRecordCount() {
    return recordCount;
  }

  public int getRequestTry() {
    return requestTry;
  }

  public byte[] getResponse() {
    return response;
  }

  public String getStatus() {
    return status;
  }

  public Platform getPlatform() {
    return platform;
  }

  @Override
  public String getRoutingKey() {
    return "crawl.response";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CrawlResponseMessage that = (CrawlResponseMessage) o;
    return attempt == that.attempt
        && requestTry == that.requestTry
        && duration == that.duration
        && Objects.equals(datasetUuid, that.datasetUuid)
        && Arrays.equals(response, that.response)
        && Objects.equals(recordCount, that.recordCount)
        && Objects.equals(status, that.status)
        && platform == that.platform;
  }

  @Override
  public int hashCode() {
    int result =
        Objects.hash(datasetUuid, attempt, requestTry, duration, recordCount, status, platform);
    result = 31 * result + Arrays.hashCode(response);
    return result;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", CrawlResponseMessage.class.getSimpleName() + "[", "]")
        .add("datasetUuid=" + datasetUuid)
        .add("attempt=" + attempt)
        .add("requestTry=" + requestTry)
        .add("response=" + Arrays.toString(response))
        .add("duration=" + duration)
        .add("recordCount=" + recordCount)
        .add("status='" + status + "'")
        .add("platform=" + platform)
        .toString();
  }
}
