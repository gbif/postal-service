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
package org.gbif.common.messaging.config;

import java.util.StringJoiner;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;

/**
 * A configuration class which holds the host and port to connect yammer metrics to a ganglia
 * server.
 */
@SuppressWarnings("PublicField")
public class GangliaConfiguration {
  private static final Logger LOG = LoggerFactory.getLogger(GangliaConfiguration.class);

  @Parameter(names = "--ganglia-host")
  @NotNull
  public String host;

  @Parameter(names = "--ganglia-port")
  public int port = 8649;

  @Parameter(names = "--ganglia-name", description = "The metrics registry name to be used")
  public String name;

  @Override
  public String toString() {
    return new StringJoiner(", ", GangliaConfiguration.class.getSimpleName() + "[", "]")
        .add("host='" + host + "'")
        .add("port=" + port)
        .add("name='" + name + "'")
        .toString();
  }
}
