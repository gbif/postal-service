/*
 * Copyright 2021 Global Biodiversity Information Facility (GBIF)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gbif.common.messaging.api.messages;

import org.gbif.api.model.crawler.DwcaValidationReport;
import org.gbif.api.vocabulary.EndpointType;
import org.gbif.api.vocabulary.OccurrenceSchemaType;
import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.google.common.base.Charsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OccurrenceFragmentedMessageTest {

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(OccurrenceFragmentedMessage.class);
  }

  public static DwcaValidationReport report(UUID uuid) {
    return new DwcaValidationReport(uuid, "fake issue");
  }

  @Test
  public void testSerDe() throws IOException {
    String test = "<foobar>deie&{  }ierndÄÖU</foobar>";
    UUID uuid = UUID.randomUUID();
    OccurrenceFragmentedMessage message =
        new OccurrenceFragmentedMessage(
            uuid,
            1,
            test.getBytes(Charsets.UTF_8),
            OccurrenceSchemaType.ABCD_1_2,
            EndpointType.BIOCASE,
            report(uuid));
    Util.testSerDe(message, OccurrenceFragmentedMessage.class);
  }

  @Test
  public void testCompareHashcode() {
    UUID datasetKey = UUID.randomUUID();
    String fragment = "<xml><inner>some fake xml asdf</inner></xml>";
    OccurrenceFragmentedMessage message1 =
        new OccurrenceFragmentedMessage(
            datasetKey,
            1,
            fragment.getBytes(Charsets.UTF_8),
            OccurrenceSchemaType.ABCD_2_0_6,
            EndpointType.BIOCASE,
            report(datasetKey));
    OccurrenceFragmentedMessage message2 =
        new OccurrenceFragmentedMessage(
            datasetKey,
            1,
            fragment.getBytes(Charsets.UTF_8),
            OccurrenceSchemaType.ABCD_2_0_6,
            EndpointType.BIOCASE,
            report(datasetKey));
    assertTrue(message1.equals(message2));
    assertEquals(message1.hashCode(), message2.hashCode());
  }

  @Test
  public void testCompareHashcodeNotEqual() {
    UUID datasetKey = UUID.randomUUID();
    byte[] raw1 = "<xml><inner>fake 1</inner></xml>".getBytes(Charsets.UTF_8);
    byte[] raw2 = "<xml><inner>fake 2</inner></xml>".getBytes(Charsets.UTF_8);
    OccurrenceFragmentedMessage message1 =
        new OccurrenceFragmentedMessage(
            datasetKey,
            1,
            raw1,
            OccurrenceSchemaType.ABCD_2_0_6,
            EndpointType.BIOCASE,
            report(datasetKey));
    OccurrenceFragmentedMessage message2 =
        new OccurrenceFragmentedMessage(
            datasetKey,
            1,
            raw2,
            OccurrenceSchemaType.ABCD_2_0_6,
            EndpointType.BIOCASE,
            report(datasetKey));
    assertFalse(message1.equals(message2));
    assertNotEquals(message1.hashCode(), message2.hashCode());
  }
}
