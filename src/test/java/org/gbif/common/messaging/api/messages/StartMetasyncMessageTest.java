package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.util.UUID;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StartMetasyncMessageTest {

  @Test
  public void testConstructor() {
    UUID uuid = UUID.randomUUID();
    StartMetasyncMessage message = new StartMetasyncMessage(uuid);
    assertThat(message.getInstallationKey()).isEqualTo(uuid);
  }

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(StartMetasyncMessage.class);
  }

  @Test
  public void testSerDe() throws IOException {
    StartMetasyncMessage message = new StartMetasyncMessage(UUID.randomUUID());
    Util.testSerDe(message, StartMetasyncMessage.class);
  }

}
