package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.util.UUID;

import com.google.common.base.Optional;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.guava.api.Assertions.assertThat;

public class StartCrawlMessageTest {

  @Test
  public void testConstructor() {
    UUID uuid = UUID.randomUUID();
    StartCrawlMessage message = new StartCrawlMessage(uuid);
    assertThat(message.getPriority()).isAbsent();

    assertThat(message.getDatasetUuid()).isEqualTo(uuid);

    message = new StartCrawlMessage(uuid, 5);
    assertThat(message.getPriority()).contains(5);

    message = new StartCrawlMessage(uuid, Optional.of(10));
    assertThat(message.getPriority()).contains(10);
  }

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(StartCrawlMessage.class);
  }

  @Test
  public void testSerDe() throws IOException {
    StartCrawlMessage message = new StartCrawlMessage(UUID.randomUUID(), Optional.of(5));
    Util.testSerDe(message, StartCrawlMessage.class);
  }

}
