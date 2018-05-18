package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Message;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;

import com.google.common.base.MoreObjects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Message to be sent when an email has to be notified about GDPR.
 */
public class GdprNotificationMessage implements Message {

  public static final String ROUTING_KEY = "gdpr.notification";

  private final String email;
  private Map<EntityType, List<UUID>> context;
  private String version;

  @JsonCreator
  public GdprNotificationMessage(
    @JsonProperty("email") String email,
    @JsonProperty("context") Map<EntityType, List<UUID>> context,
    @Nullable @JsonProperty("version") String version
  ) {
    this.email = checkNotNull(email, "email cannot be null");
    this.context = context;
    this.version = version;
  }

  public String getEmail() {
    return email;
  }

  public Map<EntityType, List<UUID>> getContext() {
    return context;
  }

  public String getVersion() {
    return version;
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GdprNotificationMessage that = (GdprNotificationMessage) o;
    return Objects.equals(email, that.email) && Objects.equals(context, that.context) && Objects.equals(version,
                                                                                                        that.version);
  }

  @Override
  public int hashCode() {
    return Objects.hash(email, context, version);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("email", email)
      .add("context", context)
      .add("version", version)
      .toString();
  }

  public enum EntityType {

    Node, Organization, Installation, Network, Dataset

  }
}
