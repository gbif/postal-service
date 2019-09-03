package org.gbif.common.messaging.api.messages;

/**
 * Enumerate the indexing platforms.
 * This is later used to decide what platform processes a message.
 */
public enum Platform {
  PIPELINES,
  OCCURRENCE,
  ALL;

  /**
   * Valdiates if a platforms is equivalent to another, i.e: are the same enum or else is {@link Platform#ALL}
   */
  public boolean equivalent(Platform platform) {
    return this == platform || Platform.ALL == this;
  }
}
