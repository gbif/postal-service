package org.gbif.common.messaging;

import junit.framework.TestCase;

public class ConnectionParametersTest extends TestCase {

  public void testToString() throws Exception {
    ConnectionParameters cp = new ConnectionParameters("localhost", 8752, "user", "pass", "/dev");
    System.out.print(cp);
  }
}