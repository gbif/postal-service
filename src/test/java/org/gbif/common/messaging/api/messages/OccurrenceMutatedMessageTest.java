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

import org.gbif.api.model.common.MediaObject;
import org.gbif.api.model.occurrence.Occurrence;
import org.gbif.api.vocabulary.EndpointType;
import org.gbif.api.vocabulary.MediaType;
import org.gbif.api.vocabulary.OccurrencePersistenceStatus;
import org.gbif.common.messaging.api.Util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import com.beust.jcommander.internal.Lists;

public class OccurrenceMutatedMessageTest {

  @Test
  public void testDefaultMessageRegistry() {
    Util.testDefaultMessageRegistry(OccurrenceMutatedMessage.class);
  }

  @Test
  public void testSerDe() throws IOException, URISyntaxException {
    Occurrence occ = new Occurrence();
    List<MediaObject> media = Lists.newArrayList();
    MediaObject medium = new MediaObject();
    medium.setType(MediaType.StillImage);
    medium.setIdentifier(new URI("http://www.example.com"));
    media.add(medium);
    occ.setMedia(media);

    OccurrenceMutatedMessage message =
        new OccurrenceMutatedMessage(
            UUID.randomUUID(),
            OccurrencePersistenceStatus.UPDATED,
            1,
            build(EndpointType.BIOCASE, 1L, UUID.randomUUID()),
            occ,
            null,
            null,
            null);
    Util.testSerDe(message, OccurrenceMutatedMessage.class);
  }

  @Test
  public void testBuilders() throws IOException {
    OccurrenceMutatedMessage msg =
        OccurrenceMutatedMessage.buildNewMessage(
            UUID.randomUUID(), build(EndpointType.BIOCASE, 1L, UUID.randomUUID()), 1);
    Util.testSerDe(msg, OccurrenceMutatedMessage.class);

    msg =
        OccurrenceMutatedMessage.buildUpdateMessage(
            UUID.randomUUID(),
            build(EndpointType.BIOCASE, 1L, UUID.randomUUID()),
            build(EndpointType.BIOCASE, 1L, UUID.randomUUID()),
            1);
    Util.testSerDe(msg, OccurrenceMutatedMessage.class);

    msg =
        OccurrenceMutatedMessage.buildDeleteMessage(
            UUID.randomUUID(),
            build(EndpointType.BIOCASE, 1L, UUID.randomUUID()),
            OccurrenceDeletionReason.OCCURRENCE_MANUAL,
            null,
            null);
    Util.testSerDe(msg, OccurrenceMutatedMessage.class);
  }

  private static Occurrence build(EndpointType prot, long key, UUID dkey) {
    Occurrence o = new Occurrence();
    o.setKey(key);
    o.setProtocol(prot);
    o.setDatasetKey(dkey);
    return o;
  }
}
