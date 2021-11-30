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
package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Message;

import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Message to send to request a new metadata synchronisation of an Installation. */
public class StartMetasyncMessage implements Message {

  public static final String ROUTING_KEY = "metasync.start";
  private final UUID installationKey;

  @JsonCreator
  public StartMetasyncMessage(@JsonProperty("installationKey") UUID installationKey) {
    this.installationKey = Objects.requireNonNull(installationKey, "installationKey can't be null");
  }

  public UUID getInstallationKey() {
    return installationKey;
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    StartMetasyncMessage that = (StartMetasyncMessage) o;
    return Objects.equals(installationKey, that.installationKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(installationKey);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", StartMetasyncMessage.class.getSimpleName() + "[", "]")
        .add("installationKey=" + installationKey)
        .toString();
  }
}
