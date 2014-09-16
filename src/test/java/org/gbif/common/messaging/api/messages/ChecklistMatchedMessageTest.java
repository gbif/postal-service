package org.gbif.common.messaging.api.messages;

import org.gbif.api.model.checklistbank.NameUsageMatch;
import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;

import com.beust.jcommander.internal.Maps;
import org.junit.Test;

public class ChecklistMatchedMessageTest {
  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(ChecklistMatchedMessage.class);
  }

  @Test
  public void testSerDe() throws IOException, URISyntaxException {
    UUID uuid = UUID.randomUUID();
    Map<NameUsageMatch.MatchType, Integer> stats = Maps.newHashMap();
    stats.put(NameUsageMatch.MatchType.EXACT, 10312);
    stats.put(NameUsageMatch.MatchType.FUZZY, 320);
    stats.put(NameUsageMatch.MatchType.HIGHERRANK, 1201);
    stats.put(NameUsageMatch.MatchType.NONE, 10);
    ChecklistMatchedMessage message = new ChecklistMatchedMessage(uuid, stats);
    Util.testSerDe(message, ChecklistMatchedMessage.class);
  }
}