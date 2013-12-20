package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import org.junit.Test;

public class DwcaDownloadFinishedMessageTest {

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(DwcaDownloadFinishedMessage.class);
  }

  @Test
  public void testSerDe() throws IOException, URISyntaxException {
    DwcaDownloadFinishedMessage message =
      new DwcaDownloadFinishedMessage(UUID.randomUUID(), new URI("http://google.de"), 1, null, true);
    Util.testSerDe(message, DwcaDownloadFinishedMessage.class);
  }
}
