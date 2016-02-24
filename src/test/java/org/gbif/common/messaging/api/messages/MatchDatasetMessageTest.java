package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.util.UUID;

import org.junit.Test;

/**
 *
 */
public class MatchDatasetMessageTest {
  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(MatchDatasetMessage.class);
  }

  @Test
  public void testSerDe() throws IOException {
    MatchDatasetMessage message =
        new MatchDatasetMessage(UUID.randomUUID());
    Util.testSerDe(message, MatchDatasetMessage.class);
  }
}