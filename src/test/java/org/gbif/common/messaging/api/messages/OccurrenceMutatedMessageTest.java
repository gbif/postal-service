package org.gbif.common.messaging.api.messages;

import org.gbif.api.model.occurrence.Occurrence;
import org.gbif.api.model.occurrence.OccurrencePersistenceStatus;
import org.gbif.api.vocabulary.EndpointType;
import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.util.UUID;

import org.junit.Test;

public class OccurrenceMutatedMessageTest {

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(OccurrenceMutatedMessage.class);
  }

  @Test
  public void testSerDe() throws IOException {
    OccurrenceMutatedMessage message =
      new OccurrenceMutatedMessage(UUID.randomUUID(), OccurrencePersistenceStatus.UPDATED, 1,
        Occurrence.builder().protocol(EndpointType.BIOCASE).key(1).datasetKey(UUID.randomUUID()).build(),
        new Occurrence(), null, null, null);
    Util.testSerDe(message, OccurrenceMutatedMessage.class);
  }

  @Test
  public void testBuilders() throws IOException {
    OccurrenceMutatedMessage msg = OccurrenceMutatedMessage.buildNewMessage(UUID.randomUUID(),
      Occurrence.builder().protocol(EndpointType.BIOCASE).key(1).datasetKey(UUID.randomUUID()).build(), 1);
    Util.testSerDe(msg, OccurrenceMutatedMessage.class);

    msg = OccurrenceMutatedMessage.buildUpdateMessage(UUID.randomUUID(),
      Occurrence.builder().protocol(EndpointType.BIOCASE).key(1).datasetKey(UUID.randomUUID()).build(),
      Occurrence.builder().protocol(EndpointType.BIOCASE).key(1).datasetKey(UUID.randomUUID()).build(), 1);
    Util.testSerDe(msg, OccurrenceMutatedMessage.class);

    msg = OccurrenceMutatedMessage.buildDeleteMessage(UUID.randomUUID(),
      Occurrence.builder().protocol(EndpointType.BIOCASE).key(1).datasetKey(UUID.randomUUID()).build(),
      OccurrenceDeletionReason.OCCURRENCE_MANUAL, null, null);
    Util.testSerDe(msg, OccurrenceMutatedMessage.class);
  }
}
