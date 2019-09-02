package org.gbif.common.messaging.api.messages;

import org.gbif.api.vocabulary.DatasetType;
import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import com.google.common.collect.Maps;
import org.junit.Test;

public class DwcaMetasyncFinishedMessageTest {

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(DwcaMetasyncFinishedMessage.class);
  }

  @Test
  public void testSerDe() throws IOException, URISyntaxException {
    UUID uuid = UUID.randomUUID();
    DwcaMetasyncFinishedMessage message = new DwcaMetasyncFinishedMessage(uuid, DatasetType.OCCURRENCE,
                                                                          new URI("http://google.de"), 1,
                                                                          Maps.<String, UUID>newHashMap(),
                                                                          OccurrenceFragmentedMessageTest.report(uuid),
                                                                          Platform.ALL);

    Util.testSerDe(message, DwcaMetasyncFinishedMessage.class);
  }
}
