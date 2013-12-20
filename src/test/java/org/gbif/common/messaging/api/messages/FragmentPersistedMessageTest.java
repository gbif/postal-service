package org.gbif.common.messaging.api.messages;

import org.gbif.api.vocabulary.OccurrencePersistenceStatus;
import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.util.UUID;

import org.junit.Test;

public class FragmentPersistedMessageTest {

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(FragmentPersistedMessage.class);
  }

  @Test
  public void testSerDe() throws IOException {
    FragmentPersistedMessage message =
      new FragmentPersistedMessage(UUID.randomUUID(), 1, OccurrencePersistenceStatus.UPDATED, 1234);
    Util.testSerDe(message, FragmentPersistedMessage.class);
  }
}
