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

import org.gbif.api.model.common.DOI;
import org.gbif.api.model.common.DoiStatus;
import org.gbif.common.messaging.api.Util;
import org.gbif.utils.file.FileUtils;

import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import com.google.common.base.Charsets;

public class ChangeDoiMessageTest {

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(ChangeDoiMessage.class);
  }

  @Test
  public void testSerDe() throws IOException {
    String xml =
        IOUtils.toString(
            FileUtils.classpathStream("datacite-example-full-v3.1.xml"), Charsets.UTF_8);
    final ChangeDoiMessage message =
        new ChangeDoiMessage(
            DoiStatus.REGISTERED, new DOI("10.999/gbif"), xml, URI.create("http://www.gbif.org"));

    Util.testSerDe(message, ChangeDoiMessage.class);
  }
}
