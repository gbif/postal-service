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

import java.util.Optional;

/**
 * Enumerate the indexing platforms. This is later used to decide what platform processes a message.
 */
public enum Platform {
  PIPELINES,
  OCCURRENCE,
  ALL;

  /**
   * Validates if a platforms is equivalent to another, i.e: are the same enum or else is {@link
   * Platform#ALL}
   */
  public boolean equivalent(Platform platform) {
    return this == platform || Platform.ALL == platform;
  }

  /**
   * Parse a string value into a recognized {@link Platform} instance.
   *
   * @param platform value to be parsed
   * @return an Optional instance with the parsed value if exists
   */
  public static Optional<Platform> parse(String platform) {
    return Optional.ofNullable(platform).map(s -> Platform.valueOf(s.trim().toUpperCase()));
  }

  /**
   * Parse a string value into a {@link Platform} instance, if not instance is recognized a default
   * value is returned.
   *
   * @param platform value to be parsed
   * @param defaultValue default
   * @return parsed platform value or defaultValue
   */
  public static Platform parseOrDefault(String platform, Platform defaultValue) {
    return Optional.ofNullable(platform)
        .map(s -> Platform.valueOf(s.trim().toUpperCase()))
        .orElse(defaultValue);
  }
}
