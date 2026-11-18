/*
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
package org.gbif.common.messaging.util;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class MessageUtils {

  private static final ObjectMapper OBJECT_MAPPER = configure(new ObjectMapper());

  /**
   * The settings postal applies to every mapper it uses: java.time support, so messages can carry
   * standard {@code LocalDateTime}, {@code Instant} etc. values. {@code java.util.Date} keeps its
   * existing form (epoch milliseconds).
   *
   * @return the same mapper
   */
  public static ObjectMapper configure(ObjectMapper mapper) {
    return mapper.registerModule(new JavaTimeModule());
  }

  public static String toString(Object message) {
    try {
      return OBJECT_MAPPER.writeValueAsString(message);
    } catch (IOException e) {
      log.warn("Failed to serialize message {}: {}", message.getClass().getSimpleName(), e.getMessage());
      return "";
    }
  }
}
