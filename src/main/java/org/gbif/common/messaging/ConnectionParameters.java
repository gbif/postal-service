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

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rabbitmq.client.ConnectionFactory;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/** Encapsulates all connection parameters necessary to connect to RabbitMQ. */
public class ConnectionParameters {

  private final String host;
  private final int port;
  private final String username;
  private final String password;
  private final String virtualHost;

  @Inject
  public ConnectionParameters(
      @Named("hostname") String host,
      @Named("port") int port,
      @Named("username") String username,
      @Named("password") String password,
      @Named("virtualhost") String virtualHost) {
    this.host = checkNotNull(host, "host can't be null");
    this.port = port;
    this.username = checkNotNull(username, "username can't be null");
    this.password = checkNotNull(password, "password can't be null");
    this.virtualHost = checkNotNull(virtualHost, "virtualHost can't be null");

    checkArgument(!host.isEmpty(), "host can't be empty");
    checkArgument(port > 0, "port has to be greater than zero");
    checkArgument(!username.isEmpty(), "username can't be empty");
    checkArgument(!password.isEmpty(), "password can't be empty");
    checkArgument(!virtualHost.isEmpty(), "virtualHost can't be empty");
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getVirtualHost() {
    return virtualHost;
  }

  ConnectionFactory getConnectionFactory() {
    ConnectionFactory connectionFactory = new ConnectionFactory();
    connectionFactory.setHost(host);
    connectionFactory.setPort(port);
    connectionFactory.setUsername(username);
    connectionFactory.setPassword(password);
    connectionFactory.setVirtualHost(virtualHost);
    return connectionFactory;
  }

  @Override
  public String toString() {
    return String.format("%s:%s@%s:%s %s", username, password, host, port, virtualHost);
  }
}
