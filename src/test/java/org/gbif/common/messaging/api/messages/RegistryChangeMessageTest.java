package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

public class RegistryChangeMessageTest {

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(RegistryChangeMessage.class);
  }

  @Test
  public void testSerDe() throws IOException, URISyntaxException {
    RegistryChangeMessage message =
      new RegistryChangeMessage(RegistryChangeMessage.ChangeType.UPDATED, String.class, "foo", "bar");
    Util.testSerDe(message, RegistryChangeMessage.class);
  }
}
