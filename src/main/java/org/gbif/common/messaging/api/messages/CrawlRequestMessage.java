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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** We send this message every time before we make a request to an endpoint. */
public class CrawlRequestMessage implements DatasetBasedMessage {

  private final UUID datasetUuid;

  private final int attempt;

  private final int requestTry;

  private final byte[] request;

  private final String status;

  @JsonCreator
  public CrawlRequestMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("attempt") int attempt,
      @JsonProperty("requestTry") int requestTry,
      @JsonProperty("request") byte[] request,
      @JsonProperty("status") String status) {

    this.datasetUuid = Objects.requireNonNull(datasetUuid, "datasetUuid can't be null");
    PreconditionUtils.checkArgument(attempt > 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    PreconditionUtils.checkArgument(requestTry > 0, "requestTry has to be greater than 0");
    this.requestTry = requestTry;
    this.request = Objects.requireNonNull(request, "request can't be null");
    this.status = Objects.requireNonNull(status, "status can't be null");
  }

  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  public int getAttempt() {
    return attempt;
  }

  public int getRequestTry() {
    return requestTry;
  }

  public byte[] getRequest() {
    return request;
  }

  /**
   * Returns a human-readable status that includes information about what is being crawled. In
   * Crawler terms this would be the current CrawlContext.
   *
   * @return human-readable status
   */
  public String getStatus() {
    return status;
  }

  @Override
  public String getRoutingKey() {
    return "crawl.request";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CrawlRequestMessage that = (CrawlRequestMessage) o;
    return attempt == that.attempt
        && requestTry == that.requestTry
        && Objects.equals(datasetUuid, that.datasetUuid)
        && Arrays.equals(request, that.request)
        && Objects.equals(status, that.status);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(datasetUuid, attempt, requestTry, status);
    result = 31 * result + Arrays.hashCode(request);
    return result;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", CrawlRequestMessage.class.getSimpleName() + "[", "]")
        .add("datasetUuid=" + datasetUuid)
        .add("attempt=" + attempt)
        .add("requestTry=" + requestTry)
        .add("request=" + Arrays.toString(request))
        .add("status='" + status + "'")
        .toString();
  }
}
