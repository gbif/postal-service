package org.gbif.common.messaging.api.messages;

import org.gbif.api.model.crawler.FinishReason;
import org.gbif.api.vocabulary.EndpointType;
import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.util.UUID;

import org.junit.Test;

public class CrawlFinishedMessageTest {

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(CrawlFinishedMessage.class);
  }

  @Test
  public void testSerDe() throws IOException {
    CrawlFinishedMessage message = new CrawlFinishedMessage(UUID.randomUUID(), 1, 1, FinishReason.NORMAL, EndpointType.DWC_ARCHIVE);
    Util.testSerDe(message, CrawlFinishedMessage.class);
  }

}
