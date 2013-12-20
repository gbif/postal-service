package org.gbif.common.messaging.api.messages;

import java.util.Arrays;
import java.util.UUID;

import com.google.common.base.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * We send this message every time before we make a request to an endpoint.
 */
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
    @JsonProperty("status") String status

  ) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    checkArgument(attempt > 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    checkArgument(requestTry > 0, "requestTry has to be greater than 0");
    this.requestTry = requestTry;
    this.request = checkNotNull(request, "request can't be null");
    this.status = checkNotNull(status, "status can't be null");
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
   * Returns a human-readable status that includes information about what is being crawled. In Crawler terms this would
   * be the current CrawlContext.
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
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof CrawlRequestMessage)) {
      return false;
    }

    final CrawlRequestMessage other = (CrawlRequestMessage) obj;
    return Objects.equal(this.datasetUuid, other.datasetUuid)
           && Objects.equal(this.attempt, other.attempt)
           && Objects.equal(this.requestTry, other.requestTry)
           && Arrays.equals(this.request, other.request)
           && Objects.equal(this.status, other.status);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(datasetUuid, attempt, requestTry, Arrays.hashCode(request), status);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
      .add("datasetUuid", datasetUuid)
      .add("attempt", attempt)
      .add("requestTry", requestTry)
      .add("request", request)
      .add("status", status)
      .toString();
  }

}
