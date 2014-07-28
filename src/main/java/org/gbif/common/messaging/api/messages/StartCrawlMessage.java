package org.gbif.common.messaging.api.messages;

import java.util.UUID;
import javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Message to send to request a new crawl of a dataset.
 * <p/>
 * A priority can be given to this request. Lower numbers mean higher priority. Requests with a higher priority will be
 * running first but there is no guarantee on the scheduling algorithm so consider this as a hint.
 */
public class StartCrawlMessage implements DatasetBasedMessage {

  private final UUID datasetUuid;

  private final Optional<Integer> priority;

  /**
   * Creates a message without an explicit priority. The crawler coordinator is free to choose a default priority in
   * this case.
   *
   * @param datasetUuid to crawl
   */
  public StartCrawlMessage(UUID datasetUuid) {
    this(datasetUuid, Optional.<Integer>absent());
  }

  @JsonCreator
  public StartCrawlMessage(
    @JsonProperty("datasetUuid") UUID datasetUuid, @JsonProperty("priority") Optional<Integer> priority
  ) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    this.priority = checkNotNull(priority, "priority can't be null");
  }

  public StartCrawlMessage(UUID datasetUuid, int priority) {
    this(datasetUuid, Optional.of(priority));
  }

  /**
   * Can be used to create a message using predefined priority constants.
   *
   * @param datasetUuid to crawl
   * @param priority    to crawl at, if none is provided a default is used
   */
  public StartCrawlMessage(UUID datasetUuid, @Nullable Priority priority) {
    this(datasetUuid, Optional.fromNullable(priority == null ? null : priority.getPriority()));
  }

  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  public Optional<Integer> getPriority() {
    return priority;
  }

  @Override
  public String getRoutingKey() {
    return "crawl.start";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof StartCrawlMessage)) {
      return false;
    }

    final StartCrawlMessage other = (StartCrawlMessage) obj;
    return Objects.equal(this.datasetUuid, other.datasetUuid) && Objects.equal(this.priority, other.priority);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(datasetUuid, priority);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("datasetUuid", datasetUuid).add("priority", priority).toString();
  }

  /**
   * Some predefined priorities that can be used to construct a message.
   */
  public enum Priority {

    LOW(10),
    NORMAL(0),
    HIGH(-10),
    CRITICAL(-100);

    private final int priority;

    Priority(int priority) {
      this.priority = priority;
    }

    public int getPriority() {
      return priority;
    }

  }

}