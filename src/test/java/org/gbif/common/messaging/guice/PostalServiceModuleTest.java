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
package org.gbif.common.messaging.guice;

import org.gbif.common.messaging.MessageListener;
import org.gbif.common.messaging.api.MessagePublisher;

import java.util.Properties;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

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
