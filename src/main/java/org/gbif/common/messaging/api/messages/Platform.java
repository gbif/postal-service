package org.gbif.common.messaging.api.messages;

import java.util.Optional;

/**
 * Enumerate the indexing platforms.
 * This is later used to decide what platform processes a message.
 */
public enum Platform {
  PIPELINES,
  OCCURRENCE,
  ALL;

  /**
   * Validates if a platforms is equivalent to another, i.e: are the same enum or else is {@link Platform#ALL}
   */
  public boolean equivalent(Platform platform) {
    return this == platform || Platform.ALL == this;
  }

  /**
   * Parse a string value into a recognized {@link Platform} instance.
   * @param platform value to be parsed
   * @return an Optional instance with the parsed value if exists
   */
  public static Optional<Platform> parse(String platform) {
    return Optional.ofNullable(platform).map(s ->  Platform.valueOf(s.trim().toUpperCase()));
  }

  /**
   * Parse a string value into a {@link Platform} instance, if not instance is recognized a default value is returned.
   * @param platform value to be parsed
   * @param defaultValue default
   * @return parsed platform value or defaultValue
   */
  public static Platform parseOrDefault(String platform, Platform defaultValue) {
    return Optional.ofNullable(platform).map(s ->  Platform.valueOf(s.trim().toUpperCase())).orElse(defaultValue);
  }
}
