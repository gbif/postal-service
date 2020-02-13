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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;

public class StartCrawlMessageTest {

  @Test
  public void testConstructor() {
    UUID uuid = UUID.randomUUID();
    StartCrawlMessage message = new StartCrawlMessage(uuid);
    assertNull(message.getPriority());

    assertThat(message.getDatasetUuid()).isEqualTo(uuid);

    message = new StartCrawlMessage(uuid, 5);
    assertThat(message.getPriority()).isEqualTo(5);

    message = new StartCrawlMessage(uuid, 10, Platform.ALL);
    assertThat(message.getPriority()).isEqualTo(10);
  }

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(StartCrawlMessage.class);
  }

  @Test
  public void testSerDe() throws IOException {
    StartCrawlMessage message = new StartCrawlMessage(UUID.randomUUID(), 5, Platform.ALL);
    Util.testSerDe(message, StartCrawlMessage.class);
  }
}
