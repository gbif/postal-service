package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.util.UUID;

import org.junit.Test;

public class CrawlErrorMessageTest {

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(CrawlErrorMessage.class);
  }

  @Test
  public void testSerDe() throws IOException {
    CrawlErrorMessage message =
      new CrawlErrorMessage(UUID.randomUUID(), 1, 1, 0, 1, "foobar", CrawlErrorMessage.ErrorType.PROTOCOL, new RuntimeException("foobar", null));
    Util.testSerDe(message, CrawlErrorMessage.class);
  }

}
