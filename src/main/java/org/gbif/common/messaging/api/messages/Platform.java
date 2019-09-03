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

  public static Optional<Platform> parse(String platform) {
    return Optional.ofNullable(platform).map(s ->  Platform.valueOf(s.trim().toUpperCase()));
  }
}
