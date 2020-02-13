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

import org.gbif.common.messaging.api.Message;
import org.gbif.common.messaging.api.MessagePublisher;

import java.io.IOException;

import com.google.common.collect.ForwardingObject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A simple object forwarding calls from this object to the delegate object.
 *
 * <p>You can extend this class and only override certain methods to add or change behavior.
 */
public class ForwardingMessagePublisher extends ForwardingObject implements MessagePublisher {

  private final MessagePublisher delegate;

  protected ForwardingMessagePublisher(MessagePublisher delegate) {
    this.delegate = checkNotNull(delegate, "delegate can't be null");
  }

  @Override
  public void send(Message message) throws IOException {
    delegate().send(message);
  }

  @Override
  public void send(Message message, boolean persistent) throws IOException {
    delegate().send(message, persistent);
  }

  @Override
  public void send(Message message, String exchange) throws IOException {
    delegate().send(message, exchange);
  }

  @Override
  public void send(Object message, String exchange, String routingKey) throws IOException {
    delegate().send(message, exchange, routingKey);
  }

  @Override
  public void send(Object message, String exchange, String routingKey, boolean persistent)
      throws IOException {
    delegate().send(message, exchange, routingKey, persistent);
  }

  @Override
  protected MessagePublisher delegate() {
    return delegate;
  }

  @Override
  public void close() {
    delegate().close();
  }
}
