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

import java.util.Objects;

import com.rabbitmq.client.ConnectionFactory;

/** Encapsulates all connection parameters necessary to connect to RabbitMQ. */
public class ConnectionParameters {

  private final String host;
  private final int port;
  private final String username;
  private final String password;
  private final String virtualHost;

  public ConnectionParameters(
      String host, // hostname
      int port, // port
      String username, // username
      String password, // password
      String virtualHost // virtualhost
      ) {
    this.host = Objects.requireNonNull(host, "host can't be null");
    this.port = port;
    this.username = Objects.requireNonNull(username, "username can't be null");
    this.password = Objects.requireNonNull(password, "password can't be null");
    this.virtualHost = Objects.requireNonNull(virtualHost, "virtualHost can't be null");

    PreconditionUtils.checkArgument(!host.isEmpty(), "host can't be empty");
    PreconditionUtils.checkArgument(port > 0, "port has to be greater than zero");
    PreconditionUtils.checkArgument(!username.isEmpty(), "username can't be empty");
    PreconditionUtils.checkArgument(!password.isEmpty(), "password can't be empty");
    PreconditionUtils.checkArgument(!virtualHost.isEmpty(), "virtualHost can't be empty");
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
