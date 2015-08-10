package org.gbif.common.messaging.api.messages;

import org.gbif.api.model.Constants;
import org.gbif.api.model.checklistbank.DatasetMetrics;
import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

public class BackboneChangedMessageTest {
  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(BackboneChangedMessage.class);
  }

  @Test
  public void testSerDe() throws IOException, URISyntaxException {
    DatasetMetrics metrics = new DatasetMetrics();
    metrics.setDatasetKey(Constants.NUB_DATASET_KEY);

    BackboneChangedMessage message = new BackboneChangedMessage(metrics);
    Util.testSerDe(message, BackboneChangedMessage.class);
  }
}