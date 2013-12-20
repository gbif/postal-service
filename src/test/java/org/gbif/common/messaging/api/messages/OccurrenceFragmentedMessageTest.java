package org.gbif.common.messaging.api.messages;

import org.gbif.api.model.crawler.DwcaValidationReport;
import org.gbif.api.vocabulary.EndpointType;
import org.gbif.api.vocabulary.OccurrenceSchemaType;
import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.util.UUID;

import com.google.common.base.Charsets;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class OccurrenceFragmentedMessageTest {

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(OccurrenceFragmentedMessage.class);
  }

  @Test
  public void testSerDe() throws IOException {
    String test = "<foobar>deie&{  }ierndÄÖU</foobar>";
    UUID uuid = UUID.randomUUID();
    OccurrenceFragmentedMessage message =
      new OccurrenceFragmentedMessage(uuid, 1, test.getBytes(Charsets.UTF_8),
        OccurrenceSchemaType.ABCD_1_2, EndpointType.BIOCASE, new DwcaValidationReport(uuid, 1, 1, 1, 1, 1, true));
    Util.testSerDe(message, OccurrenceFragmentedMessage.class);
  }

  @Test
  public void testCompareHashcode() {
    UUID datasetKey = UUID.randomUUID();
    String fragment = "<xml><inner>some fake xml asdf</inner></xml>";
    OccurrenceFragmentedMessage message1 =
      new OccurrenceFragmentedMessage(datasetKey, 1, fragment.getBytes(Charsets.UTF_8), OccurrenceSchemaType.ABCD_2_0_6,
        EndpointType.BIOCASE, new DwcaValidationReport(datasetKey, 1, 1, 1, 1, 1, true));
    OccurrenceFragmentedMessage message2 =
      new OccurrenceFragmentedMessage(datasetKey, 1, fragment.getBytes(Charsets.UTF_8), OccurrenceSchemaType.ABCD_2_0_6,
        EndpointType.BIOCASE, new DwcaValidationReport(datasetKey, 1, 1, 1, 1, 1, true));
    assertTrue(message1.equals(message2));
    assertEquals(message1.hashCode(), message2.hashCode());
  }

  @Test
  public void testCompareHashcodeNotEqual() {
    UUID datasetKey = UUID.randomUUID();
    byte[] raw1 = "<xml><inner>fake 1</inner></xml>".getBytes(Charsets.UTF_8);
    byte[] raw2 = "<xml><inner>fake 2</inner></xml>".getBytes(Charsets.UTF_8);
    OccurrenceFragmentedMessage message1 =
      new OccurrenceFragmentedMessage(datasetKey, 1, raw1, OccurrenceSchemaType.ABCD_2_0_6, EndpointType.BIOCASE,
        new DwcaValidationReport(datasetKey, 1, 1, 1, 1, 1, true));
    OccurrenceFragmentedMessage message2 =
      new OccurrenceFragmentedMessage(datasetKey, 1, raw2, OccurrenceSchemaType.ABCD_2_0_6, EndpointType.BIOCASE,
        new DwcaValidationReport(datasetKey, 1, 1, 1, 1, 1, true));
    assertFalse(message1.equals(message2));
    assertNotEquals(message1.hashCode(), message2.hashCode());
  }
}
