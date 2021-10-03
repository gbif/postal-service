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
package org.gbif.common.messaging.api;

import org.gbif.common.messaging.DefaultMessageRegistry;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Util {

  private static final MessageRegistry REGISTRY = new DefaultMessageRegistry();

  private static final ObjectMapper MAPPER = new ObjectMapper();

  static {
    MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
  }

  /**
   * Tests if the message is registered in the {@link DefaultMessageRegistry}.
   *
   * @param message to test
   */
  public static void testDefaultMessageRegistry(Class<? extends Message> message) {
    assertTrue(REGISTRY.getRegisteredMessages().contains(message));
    assertTrue(REGISTRY.getExchange(message).isPresent());
    assertTrue(REGISTRY.getGenericRoutingKey(message).isPresent());
  }

  public static <T extends Message> void testSerDe(T message, Class<T> messageClass)
      throws IOException {
    byte[] bytes = MAPPER.writeValueAsBytes(message);
    T message2 = MAPPER.readValue(bytes, messageClass);
    assertEquals(message2, message);
  }

  private Util() {
    throw new UnsupportedOperationException("Can't initialize class");
  }
}
