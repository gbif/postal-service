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
package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Message;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import com.google.common.base.Objects;

/**
 * This message will be sent every time an entity in the Registry changes (which includes deletions
 * and additions).
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
    return ROUTING_KEY
        + "."
        + changeType.toString().toLowerCase()
        + "."
        + objectClass.getSimpleName().toLowerCase();
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
    CREATED,
    UPDATED,
    DELETED
  }
}
