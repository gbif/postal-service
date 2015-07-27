package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Message;

import com.google.common.base.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * The message sent whenever the GBIF backbone has been altered.
 */
public class BackboneChangedMessage implements Message {
  public static final String ROUTING_KEY = "backbone.changed";

  private final Integer firstNewUsageKey;

  @JsonCreator
  public BackboneChangedMessage(@JsonProperty("firstNewUsageKey") Integer firstNewUsageKey) {
      this.firstNewUsageKey = firstNewUsageKey;
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }

  /**
   * @return the first and lowest newly created nub usage key. Null if none was created and it had been all updates
   */
  public Integer getFirstNewUsageKey() {
    return firstNewUsageKey;
  }

    @Override
  public int hashCode() {
    return Objects.hashCode(firstNewUsageKey);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    // THERE ARE NO PROPERTIES ON THIS CLASS
    final BackboneChangedMessage other = (BackboneChangedMessage) obj;
    return Objects.equal(this.firstNewUsageKey, other.firstNewUsageKey);
  }
}
