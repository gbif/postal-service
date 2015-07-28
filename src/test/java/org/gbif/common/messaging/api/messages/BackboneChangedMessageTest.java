package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

public class BackboneChangedMessageTest {
  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(BackboneChangedMessage.class);
  }

  @Test
  public void testSerDe() throws IOException, URISyntaxException {
    BackboneChangedMessage message = new BackboneChangedMessage();
    Util.testSerDe(message, BackboneChangedMessage.class);
  }
}