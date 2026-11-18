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
package org.gbif.common.messaging;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.AMQP;

/**
 * Per-message AMQP properties for {@link DefaultMessagePublisher#send(Object, String, String,
 * SendOptions)}. Build with {@link #builder()}; unset values keep postal's defaults (not persistent,
 * {@code text/plain}).
 */
public final class SendOptions {

  private static final String DEFAULT_CONTENT_TYPE = "text/plain";

  private final boolean persistent;
  private final String messageId;
  private final String contentType;
  private final Map<String, Object> headers;

  private SendOptions(Builder builder) {
    this.persistent = builder.persistent;
    this.messageId = builder.messageId;
    this.contentType = builder.contentType;
    this.headers = Collections.unmodifiableMap(new HashMap<>(builder.headers));
  }

  public static Builder builder() {
    return new Builder();
  }

  public boolean isPersistent() {
    return persistent;
  }

  public String getMessageId() {
    return messageId;
  }

  public String getContentType() {
    return contentType;
  }

  public Map<String, Object> getHeaders() {
    return headers;
  }

  /** The AMQP properties for these options. */
  AMQP.BasicProperties toProperties() {
    return new AMQP.BasicProperties.Builder()
        .contentType(contentType == null ? DEFAULT_CONTENT_TYPE : contentType)
        .deliveryMode(persistent ? 2 : 1)
        .priority(0)
        .messageId(messageId)
        .headers(headers.isEmpty() ? null : headers)
        .build();
  }

  public static final class Builder {
    private boolean persistent;
    private String messageId;
    private String contentType;
    private final Map<String, Object> headers = new HashMap<>();

    private Builder() {}

    /** Delivery mode 2: the broker writes the message to disk. */
    public Builder persistent(boolean persistent) {
      this.persistent = persistent;
      return this;
    }

    /** The AMQP message id, for example for deduplication by receivers. */
    public Builder messageId(String messageId) {
      this.messageId = messageId;
      return this;
    }

    public Builder contentType(String contentType) {
      this.contentType = contentType;
      return this;
    }

    public Builder header(String name, Object value) {
      headers.put(name, value);
      return this;
    }

    public SendOptions build() {
      return new SendOptions(this);
    }
  }
}
