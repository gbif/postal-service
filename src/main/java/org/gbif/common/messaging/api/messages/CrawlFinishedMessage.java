package org.gbif.common.messaging.api.messages;

import org.gbif.api.model.crawler.FinishReason;
import org.gbif.api.vocabulary.EndpointType;

import java.util.UUID;

import com.google.common.base.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * We send this every time we finish a crawl.
 */
public class CrawlFinishedMessage implements DatasetBasedMessage {

  public static final String ROUTING_KEY = "crawl.finished";

  private final UUID datasetUuid;
  private final int attempt;
  private final int totalRecordCount;
  private final FinishReason reason;
  private final EndpointType endpointType;

  @JsonCreator
  public CrawlFinishedMessage(
    @JsonProperty("datasetUuid") UUID datasetUuid,
    @JsonProperty("attempt") int attempt,
    @JsonProperty("totalRecordCount") int totalRecordCount,
    @JsonProperty("reason") FinishReason reason,
    @JsonProperty("endpointType") EndpointType endpointType
  ) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    checkArgument(attempt > 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    checkArgument(totalRecordCount >= 0, "totalRecordCount has to be greater than or equal to 0");
    this.totalRecordCount = totalRecordCount;
    this.reason = checkNotNull(reason, "reason can't be null");
    this.endpointType = endpointType;
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
           && Objects.equal(this.endpointType, other.endpointType);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(datasetUuid, attempt, totalRecordCount, reason, endpointType);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
      .add("datasetUuid", datasetUuid)
      .add("attempt", attempt)
      .add("totalRecordCount", totalRecordCount)
      .add("reason", reason)
      .add("endpointType", endpointType)
      .toString();
  }

}
