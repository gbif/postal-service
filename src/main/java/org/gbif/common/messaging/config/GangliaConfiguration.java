package org.gbif.common.messaging.config;

import javax.validation.constraints.NotNull;

import com.beust.jcommander.Parameter;
import com.google.common.base.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A configuration class which holds the host and port to connect yammer metrics to a ganglia server.
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
    return Objects.toStringHelper(this)
      .add("host", host)
      .add("port", port)
      .add("name", name)
      .toString();
  }
}
