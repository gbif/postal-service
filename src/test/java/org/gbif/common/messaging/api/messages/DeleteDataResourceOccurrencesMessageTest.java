package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Util;

import java.io.IOException;

import org.junit.Test;

public class DeleteDataResourceOccurrencesMessageTest {

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(DeleteDataResourceOccurrencesMessage.class);
  }

  @Test
  public void testSerDe() throws IOException {
    DeleteDataResourceOccurrencesMessage message = new DeleteDataResourceOccurrencesMessage(123);
    Util.testSerDe(message, DeleteDataResourceOccurrencesMessage.class);
  }
}
