package org.gbif.common.messaging.api.messages;

import org.gbif.api.model.crawler.FinishReason;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * We send this every time we finish a crawl.
 */
public class PipelinesXmlMessage implements PipelineBasedMessage {

  public static final String ROUTING_KEY = CrawlFinishedMessage.ROUTING_KEY;

  private final UUID datasetUuid;
  private final int attempt;
  private final int totalRecordCount;
  private final FinishReason reason;
  private final Set<String> pipelineSteps;

  @JsonCreator
  public PipelinesXmlMessage(@JsonProperty("datasetUuid") UUID datasetUuid, @JsonProperty("attempt") int attempt,
    @JsonProperty("totalRecordCount") int totalRecordCount, @JsonProperty("reason") FinishReason reason,
    @JsonProperty("pipelineSteps") Set<String> pipelineSteps) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    checkArgument(attempt > 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    checkArgument(totalRecordCount >= 0, "totalRecordCount has to be greater than or equal to 0");
    this.totalRecordCount = totalRecordCount;
    this.reason = checkNotNull(reason, "reason can't be null");
    this.pipelineSteps = pipelineSteps == null ? Collections.emptySet() : pipelineSteps;
  }

  public int getAttempt() {
    return attempt;
  }

  @Override
  public Set<String> getPipelineSteps() {
    return pipelineSteps;
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

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PipelinesXmlMessage that = (PipelinesXmlMessage) o;
    return attempt == that.attempt && totalRecordCount == that.totalRecordCount && Objects.equals(datasetUuid,
      that.datasetUuid) && reason == that.reason && Objects.equals(pipelineSteps, that.pipelineSteps);
  }

  @Override
  public int hashCode() {
    return Objects.hash(datasetUuid, attempt, totalRecordCount, reason, pipelineSteps);
  }

  @Override
  public String toString() {
    return "PipelinesXmlMessage{" + "datasetUuid=" + datasetUuid + ", attempt=" + attempt + ", totalRecordCount="
           + totalRecordCount + ", reason=" + reason + ", pipelineSteps=" + pipelineSteps + '}';
  }
}
