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

import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PipelinesBalancerMessage implements Message {

  public static final String ROUTING_KEY = "occurrence.pipelines.balancer";

  private String messageClass;
  private String payload;

  public PipelinesBalancerMessage() {}

  @JsonCreator
  public PipelinesBalancerMessage(
      @JsonProperty("messageClass") String messageClass, @JsonProperty("payload") String payload) {
    this.messageClass = Objects.requireNonNull(messageClass, "messageClass can't be null");
    this.payload = payload;
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }

  public String getMessageClass() {
    return messageClass;
  }

  public String getPayload() {
    return payload;
  }

  public PipelinesBalancerMessage setMessageClass(String messageClass) {
    this.messageClass = messageClass;
    return this;
  }

  public PipelinesBalancerMessage setPayload(String payload) {
    this.payload = payload;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PipelinesBalancerMessage message = (PipelinesBalancerMessage) o;
    return Objects.equals(messageClass, message.messageClass)
        && Objects.equals(payload, message.payload);
  }

  @Override
  public int hashCode() {
    return Objects.hash(messageClass, payload);
  }

  @Override
  public String toString() {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.writeValueAsString(this);
    } catch (IOException e) {
      // NOP
    }
    return "";
  }
}
