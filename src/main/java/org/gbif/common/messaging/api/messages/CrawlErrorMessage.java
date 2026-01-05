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

import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

import jakarta.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** We send this message every time we encounter an error during crawling. */
public class CrawlErrorMessage implements DatasetBasedMessage {

  private final UUID datasetUuid;

  private final int attempt;

  private final int retry;

  private final int offset;

  private final String status;

  private final ErrorType errorType;

  private final Throwable throwable;

  private final long duration;

  @JsonCreator
  public CrawlErrorMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("attempt") int attempt,
      @JsonProperty("retry") int retry,
      @JsonProperty("offset") int offset,
      @JsonProperty("duration") long duration,
      @JsonProperty("status") String status,
      @JsonProperty("errorType") ErrorType errorType,
      @Nullable @JsonProperty("throwable") Throwable throwable) {
    this.duration = duration;
    this.datasetUuid = Objects.requireNonNull(datasetUuid);
    PreconditionUtils.checkArgument(attempt > 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    PreconditionUtils.checkArgument(retry > 0, "retry has to be greater than 0");
    this.retry = retry;
    PreconditionUtils.checkArgument(offset >= 0, "offset has to be greater than or equal to 0");
    this.offset = offset;
    this.status = Objects.requireNonNull(status);
    this.errorType = Objects.requireNonNull(errorType);
    this.throwable = throwable;
  }

  public int getAttempt() {
    return attempt;
  }

  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  public long getDuration() {
    return duration;
  }

  public ErrorType getErrorType() {
    return errorType;
  }

  public int getOffset() {
    return offset;
  }

  public int getRetry() {
    return retry;
  }

  public String getStatus() {
    return status;
  }

  @Override
  public String getRoutingKey() {
    return "crawl.error" + errorType.toString().toLowerCase();
  }

  public Throwable getThrowable() {
    return throwable;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CrawlErrorMessage that = (CrawlErrorMessage) o;
    return attempt == that.attempt
        && retry == that.retry
        && offset == that.offset
        && duration == that.duration
        && Objects.equals(datasetUuid, that.datasetUuid)
        && Objects.equals(status, that.status)
        && errorType == that.errorType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(datasetUuid, attempt, retry, offset, status, errorType, duration);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", CrawlErrorMessage.class.getSimpleName() + "[", "]")
        .add("datasetUuid=" + datasetUuid)
        .add("attempt=" + attempt)
        .add("retry=" + retry)
        .add("offset=" + offset)
        .add("status='" + status + "'")
        .add("errorType=" + errorType)
        .add("throwable=" + throwable)
        .add("duration=" + duration)
        .toString();
  }

  public enum ErrorType {

    /**
     * A protocol error means some kind of problem with the response we got. This can be invalid XML
     * or things that just don't make sense.
     */
    PROTOCOL,

    /**
     * A transport error is any kind of error that's related to the transport layer like a refused
     * connection or a 404 error etc.
     */
    TRANSPORT,

    /**
     * A different error which we know what it is but doesn't fit into any of the other two
     * categories.
     */
    OTHER,

    /** An error that we didn't expect and have no idea what it is. */
    UNKNOWN
  }
}
