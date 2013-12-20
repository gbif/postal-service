package org.gbif.common.messaging.api.messages;

import java.util.UUID;
import javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * We send this message every time we encounter an error during crawling.
 */
public class CrawlErrorMessage implements DatasetBasedMessage {

  private final UUID datasetUuid;

  private final int attempt;

  private final int retry;

  private final int offset;

  private final String status;

  private final ErrorType errorType;

  private final Optional<Throwable> throwable;

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
    @Nullable @JsonProperty("throwable") Throwable throwable
  ) {
    this.duration = duration;
    this.datasetUuid = checkNotNull(datasetUuid);
    checkArgument(attempt > 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    checkArgument(retry > 0, "retry has to be greater than 0");
    this.retry = retry;
    checkArgument(offset >= 0, "offset has to be greater than or equal to 0");
    this.offset = offset;
    this.status = checkNotNull(status);
    this.errorType = checkNotNull(errorType);
    this.throwable = Optional.fromNullable(throwable);
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

  public Optional<Throwable> getThrowable() {
    return throwable;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final CrawlErrorMessage other = (CrawlErrorMessage) obj;
    return Objects.equal(this.datasetUuid, other.datasetUuid)
           && Objects.equal(this.attempt, other.attempt)
           && Objects.equal(this.retry, other.retry)
           && Objects.equal(this.offset, other.offset)
           && Objects.equal(this.status, other.status)
           && Objects.equal(this.errorType, other.errorType)
           && Objects.equal(this.duration, other.duration);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(datasetUuid, attempt, retry, offset, status, errorType, duration);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
      .add("datasetUuid", datasetUuid)
      .add("attempt", attempt)
      .add("retry", retry)
      .add("offset", offset)
      .add("status", status)
      .add("errorType", errorType)
      .add("throwable", throwable)
      .add("duration", duration)
      .toString();
  }

  public enum ErrorType {

    /**
     * A protocol error means some kind of problem with the response we got. This can be invalid XML or things that
     * just don't make sense.
     */
    PROTOCOL,

    /**
     * A transport error is any kind of error that's related to the transport layer like a refused connection or a 404
     * error etc.
     */
    TRANSPORT,

    /**
     * A different error which we know what it is but doesn't fit into any of the other two categories.
     */
    OTHER,

    /**
     * An error that we didn't expect and have no idea what it is.
     */
    UNKNOWN

  }

}
