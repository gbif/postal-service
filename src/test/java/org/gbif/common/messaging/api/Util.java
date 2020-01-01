package org.gbif.common.messaging.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import org.gbif.common.messaging.DefaultMessageRegistry;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.guava.api.Assertions.assertThat;

public class Util {

  private static final MessageRegistry REGISTRY = new DefaultMessageRegistry();

  private static final ObjectMapper MAPPER = new ObjectMapper();

  static {
    MAPPER.registerModule(new GuavaModule());
    MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
  }

  /**
   * Tests if the message is registered in the {@link DefaultMessageRegistry}.
   *
   * @param message to test
   */
  public static void testDefaultMessageRegistry(Class<? extends Message> message) {
    assertThat(REGISTRY.getRegisteredMessages()).contains(message);
    assertThat(REGISTRY.getExchange(message)).isPresent();
    assertThat(REGISTRY.getGenericRoutingKey(message)).isPresent();
  }

  public static <T extends Message> void testSerDe(T message, Class<T> messageClass) throws IOException {
    byte[] bytes = MAPPER.writeValueAsBytes(message);
    T message2 = MAPPER.readValue(bytes, messageClass);
    assertThat(message).isEqualTo(message2);
  }

  private Util() {
    throw new UnsupportedOperationException("Can't initialize class");
  }

}
