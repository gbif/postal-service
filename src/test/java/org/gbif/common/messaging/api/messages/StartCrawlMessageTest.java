package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Util;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;

public class StartCrawlMessageTest {

  @Test
  public void testConstructor() {
    UUID uuid = UUID.randomUUID();
    StartCrawlMessage message = new StartCrawlMessage(uuid);
    assertNull(message.getPriority());

    assertThat(message.getDatasetUuid()).isEqualTo(uuid);

    message = new StartCrawlMessage(uuid, 5);
    assertThat(message.getPriority()).isEqualTo(5);

    message = new StartCrawlMessage(uuid, 10, Platform.ALL);
    assertThat(message.getPriority()).isEqualTo(10);
  }

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(StartCrawlMessage.class);
  }

  @Test
  public void testSerDe() throws IOException {
    StartCrawlMessage message = new StartCrawlMessage(UUID.randomUUID(), 5, Platform.ALL);
    Util.testSerDe(message, StartCrawlMessage.class);
  }
}
