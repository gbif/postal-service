package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Message;

import java.util.UUID;

/**
 * A convenience interface for those that would like to generically handle those messages that contain dataset keys.
 */
public interface DatasetBasedMessage extends Message {

  /**
   * Returns the UUID of the dataset for this message.
   *
   * @return the dataset key
   */
  UUID getDatasetUuid();
}
