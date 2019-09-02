package org.gbif.common.messaging.api.messages;

public enum Platform {
  PIPELINES,
  OCCURRENCE,
  ALL;

  public boolean equivalent(Platform platform) {
    return Platform.ALL == this || this == platform;
  }
}
