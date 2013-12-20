package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.util.UUID;

import com.google.common.base.Optional;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CrawlResponseMessageTest {

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(CrawlResponseMessage.class);
  }

  @Test
  public void testSerDe() throws IOException {
    CrawlResponseMessage message =
      new CrawlResponseMessage(UUID.randomUUID(), 1, 1, new byte[] {1, 2, 3}, 1L, Optional.of(1), "status");
    Util.testSerDe(message, CrawlResponseMessage.class);
  }

  @Test
  public void testHashCode() {
    UUID datasetKey = UUID.randomUUID();
    CrawlResponseMessage message1 =
      new CrawlResponseMessage(datasetKey, 1, 1, new byte[] {1, 2, 3}, 1L, Optional.of(1), "status");
    CrawlResponseMessage message2 =
      new CrawlResponseMessage(datasetKey, 1, 1, new byte[] {1, 2, 3}, 1L, Optional.of(1), "status");
    assertTrue(message1.equals(message2));
    assertEquals(message1.hashCode(), message2.hashCode());
  }
}
