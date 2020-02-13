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

/** A Guice module to make it easier to include messaging in other projects. */
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
