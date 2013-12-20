package org.gbif.common.messaging.guice;

import org.gbif.common.messaging.MessageListener;
import org.gbif.common.messaging.api.MessagePublisher;

import java.util.Properties;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;

public class PostalServiceModuleTest {

  @Test
  public void testPublisher() {
    Properties props = new Properties();
    props.setProperty("test.postalservice.username", "guest");
    props.setProperty("test.postalservice.password", "guest");
    props.setProperty("test.postalservice.virtualhost", "/");
    props.setProperty("test.postalservice.hostname", "localhost");
    props.setProperty("test.postalservice.port", "5672");
    props.setProperty("test.postalservice.threadcount", "10");
    Injector injector = Guice.createInjector(new PostalServiceModule("test", props));
    injector.getProvider(MessagePublisher.class);
  }

  @Test
  public void testListener() {
    Properties props = new Properties();
    props.setProperty("test.postalservice.username", "guest");
    props.setProperty("test.postalservice.password", "guest");
    props.setProperty("test.postalservice.virtualhost", "/");
    props.setProperty("test.postalservice.hostname", "localhost");
    props.setProperty("test.postalservice.port", "5672");
    props.setProperty("test.postalservice.threadcount", "10");
    Injector injector = Guice.createInjector(new PostalServiceModule("test", props));
    injector.getProvider(MessageListener.class);
  }

}
