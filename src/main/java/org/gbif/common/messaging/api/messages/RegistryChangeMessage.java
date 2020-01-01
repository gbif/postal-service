package org.gbif.common.messaging.api.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Objects;
import org.gbif.common.messaging.api.Message;

/**
 * This message will be sent every time an entity in the Registry changes (which includes deletions and additions).
 */
public class RegistryChangeMessage implements Message {

  private static final String ROUTING_KEY = "registry.change";
  private final ChangeType changeType;
  private final Class<?> objectClass;
  private final Object oldObject;
  private final Object newObject;

  @JsonCreator
  public RegistryChangeMessage(
    @JsonProperty("changeType") ChangeType changeType,
    @JsonProperty("objectClass") Class<?> objectClass,
    @JsonProperty("oldObject") Object oldObject,
    @JsonProperty("newObject") Object newObject) {
    this.changeType = changeType;
    this.objectClass = objectClass;
    this.oldObject = oldObject;
    this.newObject = newObject;
  }

  public ChangeType getChangeType() {
    return changeType;
  }

  public Class<?> getObjectClass() {
    return objectClass;
  }

  @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "objectClass")
  public Object getOldObject() {
    return oldObject;
  }

  @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "objectClass")
  public Object getNewObject() {
    return newObject;
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY + "." + changeType.toString().toLowerCase() + "." + objectClass.getSimpleName().toLowerCase();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof RegistryChangeMessage)) {
      return false;
    }

    RegistryChangeMessage other = (RegistryChangeMessage) obj;
    return Objects.equal(this.changeType, other.changeType)
      && Objects.equal(this.objectClass, other.objectClass)
      && Objects.equal(this.oldObject, other.oldObject)
      && Objects.equal(this.newObject, other.newObject);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(changeType, objectClass, oldObject, newObject);
  }

  public enum ChangeType {
    CREATED, UPDATED, DELETED
  }
}
