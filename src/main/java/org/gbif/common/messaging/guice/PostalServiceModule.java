package org.gbif.common.messaging.guice;

import org.gbif.common.messaging.ConnectionParameters;
import org.gbif.common.messaging.DefaultMessagePublisher;
import org.gbif.common.messaging.MessageListener;
import org.gbif.common.messaging.api.MessagePublisher;
import org.gbif.service.guice.PrivateServiceModule;

import java.io.IOException;
import java.util.Properties;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * A Guice module to make it easier to include messaging in other projects.
 */
public class PostalServiceModule extends PrivateServiceModule {

  private static final String INFIX = ".postalservice.";

  public PostalServiceModule(String prefix, Properties properties) {
    super(prefix + INFIX, properties);
  }

  @Override
  protected void configureService() {
    bind(ConnectionParameters.class);
    expose(MessagePublisher.class);
    expose(MessageListener.class);
  }

  @Provides
  @Singleton
  @Inject
  public MessagePublisher provideMessagePublisher(ConnectionParameters conf) throws IOException {
    return new DefaultMessagePublisher(conf);
  }

  @Provides
  @Singleton
  @Inject
  public MessageListener provideMessageListener(ConnectionParameters conf) throws IOException {
    return new MessageListener(conf);
  }

}
