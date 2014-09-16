package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

import org.junit.Test;

public class ChecklistAnalyzedMessageTest {
  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(ChecklistAnalyzedMessage.class);
  }

  @Test
  public void testSerDe() throws IOException, URISyntaxException {
    UUID uuid = UUID.randomUUID();
    ChecklistAnalyzedMessage message = new ChecklistAnalyzedMessage(uuid);
    Util.testSerDe(message, ChecklistAnalyzedMessage.class);
  }
}