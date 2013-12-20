package org.gbif.common.messaging.api.messages;

import org.gbif.api.model.occurrence.Occurrence;
import org.gbif.api.vocabulary.OccurrencePersistenceStatus;
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
      build(EndpointType.BIOCASE, 1, UUID.randomUUID()),
      new Occurrence(), null, null, null);
    Util.testSerDe(message, OccurrenceMutatedMessage.class);
  }

  @Test
  public void testBuilders() throws IOException {
    OccurrenceMutatedMessage msg = OccurrenceMutatedMessage.buildNewMessage(UUID.randomUUID(),
      build(EndpointType.BIOCASE, 1, UUID.randomUUID()), 1);
    Util.testSerDe(msg, OccurrenceMutatedMessage.class);

    msg = OccurrenceMutatedMessage.buildUpdateMessage(UUID.randomUUID(),
                                                      build(EndpointType.BIOCASE, 1, UUID.randomUUID()),
                                                      build(EndpointType.BIOCASE, 1, UUID.randomUUID()), 1);
    Util.testSerDe(msg, OccurrenceMutatedMessage.class);

    msg = OccurrenceMutatedMessage.buildDeleteMessage(UUID.randomUUID(),
      build(EndpointType.BIOCASE, 1, UUID.randomUUID()),
      OccurrenceDeletionReason.OCCURRENCE_MANUAL, null, null);
    Util.testSerDe(msg, OccurrenceMutatedMessage.class);
  }

  private Occurrence build(EndpointType prot, int key, UUID dkey) {
    Occurrence o = new Occurrence();
    o.setKey(key);
    o.setProtocol(prot);
    o.setDatasetKey(dkey);
    return o;
  }
}
