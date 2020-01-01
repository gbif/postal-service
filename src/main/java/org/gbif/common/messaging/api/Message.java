package org.gbif.common.messaging.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * By implementing this interface messages publishers and subscribers can use some convenience methods.
 */
public interface Message {

  /**
   * Returns the routing key for this message which might depend on its internal state.
   *
   * @return routing key for this specific instance of the message
   */
  @JsonIgnore
  String getRoutingKey();

}
