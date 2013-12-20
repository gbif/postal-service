package org.gbif.common.messaging;

import com.rabbitmq.client.ConnectionFactory;

import static org.mockito.Mockito.mock;

public class MockConnectionParameters extends ConnectionParameters {

  private final ConnectionFactory connectionFactory = mock(ConnectionFactory.class);

  public MockConnectionParameters() {
    super("host", 1, "username", "password", "virtualHost");
  }

  @Override
  ConnectionFactory getConnectionFactory() {
    return connectionFactory;
  }

}
