package org.gbif.common.messaging;

import org.gbif.common.messaging.api.MessagePublisher;

import java.io.IOException;

import com.google.common.util.concurrent.RateLimiter;

import static com.google.common.base.Preconditions.checkArgument;

public class RateLimitingMessagePublisher extends ForwardingMessagePublisher {

  private final RateLimiter rateLimiter;

  public RateLimitingMessagePublisher(MessagePublisher delegate, int requestsPerSecond) {
    super(delegate);
    checkArgument(requestsPerSecond > 0, "requestsPerSecond has to be greater than zero");
    rateLimiter = RateLimiter.create(requestsPerSecond);
  }

  @Override
  public void send(Object message, String exchange, String routingKey) throws IOException {
    rateLimiter.acquire();
    super.send(message, exchange, routingKey);
  }

}
