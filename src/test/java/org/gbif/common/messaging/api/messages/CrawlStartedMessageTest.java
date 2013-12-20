package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import org.junit.Test;

public class CrawlStartedMessageTest {

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(CrawlStartedMessage.class);
  }

  @Test
  public void testSerDe() throws IOException {
    URI uri = URI.create("http://www.gbif.org/index.html");
    CrawlStartedMessage message = new CrawlStartedMessage(UUID.randomUUID(), 1, uri, "foobar");
    Util.testSerDe(message, CrawlStartedMessage.class);

  }

}
