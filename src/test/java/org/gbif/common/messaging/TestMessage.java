package org.gbif.common.messaging;

import org.gbif.common.messaging.api.Message;

/**
 * A simple message that can be used for testing.
 */
class TestMessage implements Message {

  public String foo = "foo";

  @Override
  public String getRoutingKey() {
    return "foobar";
  }
}
