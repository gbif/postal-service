package org.gbif.common.messaging.api.messages;

import org.gbif.api.model.checklistbank.DatasetMetrics;
import org.gbif.common.messaging.api.Message;

import java.util.Objects;

import com.google.common.base.Preconditions;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The message sent whenever the GBIF backbone has been altered.
 */
public class BackboneChangedMessage implements Message {
  public static final String ROUTING_KEY = "backbone.changed";

  private final DatasetMetrics metrics;

  @JsonCreator
  public BackboneChangedMessage(@JsonProperty("metrics") DatasetMetrics metrics) {
    this.metrics = Preconditions.checkNotNull(metrics);
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }

  public DatasetMetrics getMetrics() {
    return metrics;
  }

  @Override
  public int hashCode() {
    return metrics.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final BackboneChangedMessage other = (BackboneChangedMessage) obj;
    return Objects.equals(this.metrics, other.metrics);
  }
}
