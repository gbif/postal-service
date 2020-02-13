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
