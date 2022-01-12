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

import java.net.URI;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** We send this message every time we actively start to crawl a dataset. */
public class CrawlStartedMessage implements DatasetBasedMessage {

  private final UUID datasetUuid;

  private final int attempt;

  private final URI targetUrl;

  private final String status;

  @JsonCreator
  public CrawlStartedMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("attempt") int attempt,
      @JsonProperty("targetUrl") URI targetUrl,
      @JsonProperty("status") String status) {
    PreconditionUtils.checkArgument(attempt > 0, "attempt has to be greater than 0");
    this.attempt = attempt;

    this.datasetUuid = Objects.requireNonNull(datasetUuid, "datasetUuid can't be null");
    this.targetUrl = Objects.requireNonNull(targetUrl, "targetUrl can't be null");
    this.status = Objects.requireNonNull(status, "status can't be null");
  }

  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  public int getAttempt() {
    return attempt;
  }

  public URI getTargetUrl() {
    return targetUrl;
  }

  /**
   * This is meant to be a human-readable string to convey more details or a status about the crawl
   * being started.
   *
   * <p>This should usually include a full representation of the crawl being run including the
   * protocol dependent information.
   *
   * @return a human-readable status of this crawl start
   */
  public String getStatus() {
    return status;
  }

  @Override
  public String getRoutingKey() {
    return "crawl.started";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CrawlStartedMessage that = (CrawlStartedMessage) o;
    return attempt == that.attempt
        && Objects.equals(datasetUuid, that.datasetUuid)
        && Objects.equals(targetUrl, that.targetUrl)
        && Objects.equals(status, that.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(datasetUuid, attempt, targetUrl, status);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", CrawlStartedMessage.class.getSimpleName() + "[", "]")
        .add("datasetUuid=" + datasetUuid)
        .add("attempt=" + attempt)
        .add("targetUrl=" + targetUrl)
        .add("status='" + status + "'")
        .toString();
  }
}
