package org.gbif.common.messaging.api.messages;

import org.gbif.api.model.common.DOI;
import org.gbif.api.model.common.DoiStatus;
import org.gbif.common.messaging.api.Util;
import org.gbif.utils.file.FileUtils;

import java.io.IOException;
import java.net.URI;

import com.google.common.base.Charsets;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class ChangeDoiMessageTest {

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(ChangeDoiMessage.class);
  }

  @Test
  public void testSerDe() throws IOException {
    String xml = IOUtils.toString(FileUtils.classpathStream("datacite-example-full-v3.1.xml"), Charsets.UTF_8);
    final ChangeDoiMessage message = new ChangeDoiMessage(DoiStatus.REGISTERED, new DOI("10.999/gbif"), xml,
      URI.create("http://www.gbif.org"));

    Util.testSerDe(message, ChangeDoiMessage.class);
  }
}