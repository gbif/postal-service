package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

import org.junit.Test;


public class ChecklistSyncedMessageTest {

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(ChecklistSyncedMessage.class);
  }

  @Test
  public void testSerDe() throws IOException, URISyntaxException {
    UUID uuid = UUID.randomUUID();
    ChecklistSyncedMessage message = new ChecklistSyncedMessage(uuid, 123456, 1234);
    Util.testSerDe(message, ChecklistSyncedMessage.class);
  }
}