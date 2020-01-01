package org.gbif.common.messaging.api.messages;

import java.util.Arrays;
import java.util.UUID;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * We send this message every time we get a response from an endpoint.
 */
public class CrawlResponseMessage implements DatasetBasedMessage {

  private final UUID datasetUuid;

  private final int attempt;

  private final int requestTry;

  private final byte[] response;

  private final long duration;

  private final Optional<Integer> recordCount;

  private final String status;

  private final Platform platform;

  @JsonCreator
  public CrawlResponseMessage(
    @JsonProperty("datasetUuid") UUID datasetUuid,
    @JsonProperty("attempt") int attempt,
    @JsonProperty("requestTry") int requestTry,
    @JsonProperty("response") byte[] response,
    @JsonProperty("duration") long duration,
    @JsonProperty("recordCount") Optional<Integer> recordCount,
    @JsonProperty("status") String status,
    @JsonProperty("platform") Platform platform
  ) {
    this.datasetUuid = checkNotNull(datasetUuid);

    checkArgument(attempt > 0, "attempt has to be greater than 0");
    this.attempt = attempt;

    checkArgument(requestTry > 0, "requestTry has to be greater than 0");
    this.requestTry = requestTry;

    this.response = checkNotNull(response, "response can't be null");

    checkArgument(duration > 0, "duration has to be greater than 0");
    this.duration = duration;

    this.recordCount = checkNotNull(recordCount, "recordCount can't be null");
    checkArgument(!recordCount.isPresent() || recordCount.get() >= 0,
                  "recordCount has to be absent or greater than or equal to 0");

    this.status = checkNotNull(status, "status can't be null");

    this.platform = java.util.Optional.ofNullable(platform).orElse(Platform.ALL);

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

  public Optional<Integer> getRecordCount() {
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
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof CrawlResponseMessage)) {
      return false;
    }

    final CrawlResponseMessage other = (CrawlResponseMessage) obj;
    return Objects.equal(this.datasetUuid, other.datasetUuid)
           && Objects.equal(this.attempt, other.attempt)
           && Objects.equal(this.requestTry, other.requestTry)
           && Arrays.equals(this.response, other.response)
           && Objects.equal(this.duration, other.duration)
           && Objects.equal(this.recordCount, other.recordCount)
           && Objects.equal(this.status, other.status);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(datasetUuid, attempt, requestTry, Arrays.hashCode(response), duration, recordCount, status);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
      .add("datasetUuid", datasetUuid)
      .add("attempt", attempt)
      .add("requestTry", requestTry)
      .add("response", response)
      .add("duration", duration)
      .add("recordCount", recordCount)
      .add("status", status)
      .toString();
  }

}
