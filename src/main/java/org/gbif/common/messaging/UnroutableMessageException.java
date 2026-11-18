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

import java.io.IOException;

/**
 * Thrown by a publisher built with {@link DefaultMessagePublisher.Builder#mandatory(boolean)} when
 * the broker returns a message because no queue is bound for its routing key. The message was not
 * delivered anywhere. Retrying immediately does not help, so postal does not retry it.
 */
public class UnroutableMessageException extends IOException {

  private final String exchange;
  private final String routingKey;
  private final int replyCode;

  public UnroutableMessageException(
      String exchange, String routingKey, int replyCode, String replyText) {
    super(
        String.format(
            "Message to exchange [%s] with routing key [%s] was returned by the broker: %d %s",
            exchange, routingKey, replyCode, replyText));
    this.exchange = exchange;
    this.routingKey = routingKey;
    this.replyCode = replyCode;
  }

  public String getExchange() {
    return exchange;
  }

  public String getRoutingKey() {
    return routingKey;
  }

  public int getReplyCode() {
    return replyCode;
  }
}
