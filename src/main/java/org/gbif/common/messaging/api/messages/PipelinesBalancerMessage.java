package org.gbif.common.messaging.api.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gbif.common.messaging.api.Message;

import java.io.IOException;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public class PipelinesBalancerMessage implements Message {

  public static final String ROUTING_KEY = "occurrence.pipelines.balancer";

  private String messageClass;
  private String payload;

  public PipelinesBalancerMessage() {
  }

  @JsonCreator
  public PipelinesBalancerMessage(
      @JsonProperty("messageClass") String messageClass,
      @JsonProperty("payload") String payload) {
    this.messageClass = checkNotNull(messageClass, "messageClass can't be null");
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
    return Objects.equals(messageClass, message.messageClass) &&
        Objects.equals(payload, message.payload);
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
