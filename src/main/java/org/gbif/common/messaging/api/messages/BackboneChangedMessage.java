package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Message;

import org.codehaus.jackson.annotate.JsonCreator;

/**
 * The message sent whenever the GBIF backbone has been altered.
 */
public class BackboneChangedMessage implements Message {
  public static final String ROUTING_KEY = "backbone.changed";

  @JsonCreator
  public BackboneChangedMessage() {

  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }

  @Override
  public int hashCode() {
    // THERE ARE NO PROPERTIES ON THIS CLASS
    return 32131278;
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
    return true;
  }
}
