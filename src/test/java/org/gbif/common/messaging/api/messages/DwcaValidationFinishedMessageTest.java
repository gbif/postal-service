package org.gbif.common.messaging.api.messages;

import org.gbif.api.model.crawler.DwcaValidationReport;
import org.gbif.api.vocabulary.DatasetType;
import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import org.junit.Test;

public class DwcaValidationFinishedMessageTest {

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(DwcaValidationFinishedMessage.class);
  }

  @Test
  public void testSerDe() throws IOException, URISyntaxException {
    UUID uuid = UUID.randomUUID();
    DwcaValidationFinishedMessage message = new DwcaValidationFinishedMessage(uuid,
                            DatasetType.OCCURRENCE, new URI("http://google.de"), 1,
                            new DwcaValidationReport(uuid, 1, 1, 1, 1, 1, true));
    Util.testSerDe(message, DwcaValidationFinishedMessage.class);
  }
}