package org.gbif.common.messaging.api.messages;

import org.gbif.api.model.occurrence.OccurrencePersistenceStatus;
import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.util.UUID;

import org.junit.Test;

public class VerbatimPersistedMessageTest {

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(VerbatimPersistedMessage.class);
  }

  @Test
  public void testSerDe() throws IOException {
    VerbatimPersistedMessage message =
      new VerbatimPersistedMessage(UUID.randomUUID(), 1, OccurrencePersistenceStatus.UPDATED, 1234);
    Util.testSerDe(message, VerbatimPersistedMessage.class);
  }
}
