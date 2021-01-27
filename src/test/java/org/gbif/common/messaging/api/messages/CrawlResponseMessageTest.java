/*
 * Copyright 2020 Global Biodiversity Information Facility (GBIF)
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

import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.google.common.base.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CrawlResponseMessageTest {

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(CrawlResponseMessage.class);
  }

  @Test
  public void testSerDe() throws IOException {
    CrawlResponseMessage message =
        new CrawlResponseMessage(
            UUID.randomUUID(),
            1,
            1,
            new byte[] {1, 2, 3},
            1L,
            Optional.of(1),
            "status",
            Platform.ALL);
    Util.testSerDe(message, CrawlResponseMessage.class);
  }

  @Test
  public void testHashCode() {
    UUID datasetKey = UUID.randomUUID();
    CrawlResponseMessage message1 =
        new CrawlResponseMessage(
            datasetKey, 1, 1, new byte[] {1, 2, 3}, 1L, Optional.of(1), "status", Platform.ALL);
    CrawlResponseMessage message2 =
        new CrawlResponseMessage(
            datasetKey, 1, 1, new byte[] {1, 2, 3}, 1L, Optional.of(1), "status", Platform.ALL);
    assertEquals(message1, message2);
    assertEquals(message1.hashCode(), message2.hashCode());
  }
}
