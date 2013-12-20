package org.gbif.common.messaging.api;

/**
 * Called for every new message. Implementations of this class need to be thread-safe.
 * <p/>
 * This interface does not handle task abortions. Implementations are free to check their Threads interrupted status
 * but are not required to.
 *
 * @param <T> of the message to process
 */
public interface MessageCallback<T> {

  /**
   * Called every time a new message is ready to process. Exceptions being thrown are logged but otherwise ignored.
   *
   * @param message to process
   */
  void handleMessage(T message);

  /**
   * Returns the class of the message to be handled (i.e. the type {@code T}.
   *
   * @return class of the message to be handled
   */
  Class<T> getMessageClass();

}
