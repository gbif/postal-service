package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Util;

import java.io.IOException;

import org.junit.Test;

public class DeleteOccurrenceMessageTest {

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(DeleteOccurrenceMessage.class);
  }

  @Test
  public void testSerDe() throws IOException {
    DeleteOccurrenceMessage message =
      new DeleteOccurrenceMessage(1, OccurrenceDeletionReason.DATASET_MANUAL, null, null);
    Util.testSerDe(message, DeleteOccurrenceMessage.class);

    message =
      new DeleteOccurrenceMessage(1, OccurrenceDeletionReason.NOT_SEEN_IN_LAST_CRAWL, 1, 2);
    Util.testSerDe(message, DeleteOccurrenceMessage.class);
  }
}
