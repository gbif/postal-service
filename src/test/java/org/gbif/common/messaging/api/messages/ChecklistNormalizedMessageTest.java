package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

import org.junit.Test;


public class ChecklistNormalizedMessageTest {

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(ChecklistNormalizedMessage.class);
  }

  @Test
  public void testSerDe() throws IOException, URISyntaxException {
    UUID uuid = UUID.randomUUID();
    ChecklistNormalizedMessage message = new ChecklistNormalizedMessage(uuid);
    Util.testSerDe(message, ChecklistNormalizedMessage.class);
  }
}