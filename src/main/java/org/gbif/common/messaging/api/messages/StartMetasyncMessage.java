package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Message;

import java.util.UUID;

import com.google.common.base.Objects;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Message to send to request a new metadata synchronisation of an Installation.
 */
public class StartMetasyncMessage implements Message {

  public static final String ROUTING_KEY = "metasync.start";
  private final UUID installationKey;

  @JsonCreator
  public StartMetasyncMessage(@JsonProperty("installationKey") UUID installationKey) {
    this.installationKey = checkNotNull(installationKey, "installationKey can't be null");
  }

  public UUID getInstallationKey() {
    return installationKey;
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(installationKey);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof StartMetasyncMessage)) {
      return false;
    }

    StartMetasyncMessage other = (StartMetasyncMessage) obj;
    return Objects.equal(this.installationKey, other.installationKey);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("installationKey", installationKey).toString();
  }

}
