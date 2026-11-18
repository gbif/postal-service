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

import org.gbif.utils.PreconditionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * What {@link MessageListener#listen(QueueBinding, org.gbif.common.messaging.api.MessageCallback)}
 * declares and consumes: a durable queue bound to a durable topic exchange, with optional queue
 * arguments and an optional dead-letter setup.
 *
 * <p>With {@link Builder#deadLetter(String)}, rejected and nacked messages (postal never requeues
 * them) are routed to the dead-letter exchange with the queue name as routing key, and kept in the
 * durable queue {@code <queue>.dlq}.
 *
 * <p>RabbitMQ refuses to redeclare an existing queue with different arguments, so adding a
 * dead-letter exchange to a queue that already exists without one needs the queue to be deleted
 * first, or a broker policy instead.
 */
public final class QueueBinding {

  public static final String DEAD_LETTER_QUEUE_SUFFIX = ".dlq";

  private final String queue;
  private final String exchange;
  private final String routingKey;
  private final int threads;
  private final String deadLetterExchange;
  private final Map<String, Object> arguments;

  private QueueBinding(Builder builder) {
    this.queue = builder.queue;
    this.exchange = builder.exchange;
    this.routingKey = builder.routingKey;
    this.threads = builder.threads;
    this.deadLetterExchange = builder.deadLetterExchange;
    Map<String, Object> args = new HashMap<>(builder.arguments);
    if (deadLetterExchange != null) {
      args.put("x-dead-letter-exchange", deadLetterExchange);
      args.put("x-dead-letter-routing-key", queue);
    }
    this.arguments = Collections.unmodifiableMap(args);
  }

  public static Builder builder(String queue, String exchange, String routingKey) {
    return new Builder(queue, exchange, routingKey);
  }

  public String getQueue() {
    return queue;
  }

  public String getExchange() {
    return exchange;
  }

  public String getRoutingKey() {
    return routingKey;
  }

  public int getThreads() {
    return threads;
  }

  /** The dead-letter exchange, or null when dead-lettering is not configured. */
  public String getDeadLetterExchange() {
    return deadLetterExchange;
  }

  /** The dead-letter queue, or null when dead-lettering is not configured. */
  public String getDeadLetterQueue() {
    return deadLetterExchange == null ? null : queue + DEAD_LETTER_QUEUE_SUFFIX;
  }

  /** Queue arguments, including the dead-letter arguments; empty when there are none. */
  public Map<String, Object> getArguments() {
    return arguments;
  }

  @Override
  public String toString() {
    return String.format(
        "QueueBinding[queue=%s, exchange=%s, routingKey=%s, threads=%d, deadLetterExchange=%s]",
        queue, exchange, routingKey, threads, deadLetterExchange);
  }

  public static final class Builder {
    private final String queue;
    private final String exchange;
    private final String routingKey;
    private int threads = 1;
    private String deadLetterExchange;
    private final Map<String, Object> arguments = new HashMap<>();

    private Builder(String queue, String exchange, String routingKey) {
      this.queue = Objects.requireNonNull(queue, "queue can't be null");
      this.exchange = Objects.requireNonNull(exchange, "exchange can't be null");
      this.routingKey = Objects.requireNonNull(routingKey, "routingKey can't be null");
    }

    /** Number of consumer threads; at least 1. Default 1. */
    public Builder threads(int threads) {
      PreconditionUtils.checkArgument(
          threads >= 1, "threads needs to be greater than or equal to 1");
      this.threads = threads;
      return this;
    }

    /** Declare the queue with this dead-letter exchange, and declare the exchange and DLQ too. */
    public Builder deadLetter(String deadLetterExchange) {
      this.deadLetterExchange =
          Objects.requireNonNull(deadLetterExchange, "deadLetterExchange can't be null");
      return this;
    }

    /** A queue argument such as {@code x-message-ttl} or {@code x-queue-type}. */
    public Builder queueArgument(String name, Object value) {
      arguments.put(Objects.requireNonNull(name, "name can't be null"), value);
      return this;
    }

    public QueueBinding build() {
      return new QueueBinding(this);
    }
  }
}
