/*
 * Copyright 2021 Global Biodiversity Information Facility (GBIF)
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
package org.gbif.common.messaging.config;

import org.gbif.common.messaging.ConnectionParameters;

import java.io.IOException;

import javax.validation.constraints.NotNull;

import com.beust.jcommander.Parameter;
import com.google.common.base.MoreObjects;
import com.rabbitmq.client.AMQP;

/**
 * A configuration class which can be used to get all the details needed to create a connection to
 * RabbitMQ.
 *
 * <p>
 */
@SuppressWarnings("PublicField")
public class MessagingConfiguration {

  @Parameter(names = "--messaging-host")
  @NotNull
  public String host;

  @Parameter(names = "--messaging-virtual-host")
  @NotNull
  public String virtualHost;

  @Parameter(names = "--messaging-username")
  @NotNull
  public String username;

  @Parameter(names = "--messaging-password", password = true)
  @NotNull
  public String password;

  @Parameter(names = "--messaging-port")
  public int port = AMQP.PROTOCOL.PORT;

  public ConnectionParameters getConnectionParameters() throws IOException {
    return new ConnectionParameters(host, port, username, password, virtualHost);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("host", host)
        .add("virtualHost", virtualHost)
        .add("port", port)
        .add("username", username)
        .add("password", password)
        .toString();
  }
}
