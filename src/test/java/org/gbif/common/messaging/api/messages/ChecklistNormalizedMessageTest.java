package org.gbif.common.messaging.api.messages;

import org.gbif.api.model.crawler.NormalizerStats;
import org.gbif.api.vocabulary.Origin;
import org.gbif.api.vocabulary.Rank;
import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import org.junit.Test;


public class ChecklistNormalizedMessageTest {

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(ChecklistNormalizedMessage.class);
  }

  @Test
  public void testSerDe() throws IOException, URISyntaxException {
    UUID uuid = UUID.randomUUID();
    NormalizerStats stats =
      new NormalizerStats(2, 7, 20, Maps.<Origin, Integer>newHashMap(), Maps.<Rank, Integer>newHashMap(),
        Lists.<String>newArrayList());
    ChecklistNormalizedMessage message = new ChecklistNormalizedMessage(uuid, stats);
    Util.testSerDe(message, ChecklistNormalizedMessage.class);
  }
}