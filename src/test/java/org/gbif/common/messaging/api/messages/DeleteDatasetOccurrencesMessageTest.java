package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.util.UUID;

import org.junit.Test;

public class DeleteDatasetOccurrencesMessageTest {

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(DeleteDatasetOccurrencesMessage.class);
  }

  @Test
  public void testSerDe() throws IOException {
    DeleteDatasetOccurrencesMessage message =
      new DeleteDatasetOccurrencesMessage(UUID.randomUUID(), OccurrenceDeletionReason.DATASET_MANUAL);
    Util.testSerDe(message, DeleteDatasetOccurrencesMessage.class);
  }
}
