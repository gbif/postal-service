package org.gbif.common.messaging;

import org.gbif.common.messaging.api.Message;
import org.gbif.common.messaging.api.MessageRegistry;
import org.gbif.common.messaging.api.messages.*;

import java.util.concurrent.ConcurrentMap;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A default implementation of the {@link MessageRegistry} interface where all the messages from this projects {@code
 * org.gbif.common.messaging.api} package are already preregistered.
 * <p/>
 * This class is thread-safe.
 */
@ThreadSafe
public class DefaultMessageRegistry implements MessageRegistry {

  private final Object lock = new Object();

  /* These are the default messages which are the seed for any new instance */
  private static final ImmutableMap<Class<? extends Message>, String> MESSAGE_TO_EXCHANGE_MAPPING;
  private static final ImmutableMap<Class<? extends Message>, String> MESSAGE_TO_ROUTING_KEY_MAPPING;

  @GuardedBy("lock")
  private final ConcurrentMap<Class<? extends Message>, String> exchangeMapping = Maps.newConcurrentMap();
  @GuardedBy("lock")
  private final ConcurrentMap<Class<? extends Message>, String> routingKeyMapping = Maps.newConcurrentMap();

  static {
    MESSAGE_TO_EXCHANGE_MAPPING = ImmutableMap.<Class<? extends Message>, String>builder()
      .put(AbcdaDownloadFinishedMessage.class, "crawler")
      .put(CrawlErrorMessage.class, "crawler")
      .put(CrawlFinishedMessage.class, "crawler")
      .put(CrawlStartedMessage.class, "crawler")
      .put(CrawlRequestMessage.class, "crawler")
      .put(CrawlResponseMessage.class, "crawler")
      .put(OccurrenceFragmentedMessage.class, "crawler")
      .put(DwcaDownloadFinishedMessage.class, "crawler")
      .put(DwcaMetasyncFinishedMessage.class, "crawler")
      .put(DwcaValidationFinishedMessage.class, "crawler")
      .put(ExtendedRecordAvailableMessage.class, "crawler")
      .put(FragmentPersistedMessage.class, "occurrence")
      .put(VerbatimPersistedMessage.class, "occurrence")
      .put(OccurrenceMutatedMessage.class, "occurrence")
      .put(DeleteOccurrenceMessage.class, "occurrence")
      .put(DeleteDataResourceOccurrencesMessage.class, "occurrence")
      .put(DeleteDatasetOccurrencesMessage.class, "occurrence")
      .put(ParseFragmentMessage.class, "occurrence")
      .put(ParseDatasetMessage.class, "occurrence")
      .put(InterpretVerbatimMessage.class, "occurrence")
      .put(InterpretDatasetMessage.class, "occurrence")
      .put(IndexDatasetMessage.class, "occurrence")
      .put(RegistryChangeMessage.class, "registry")
      .put(StartCrawlMessage.class, "registry")
      .put(StartMetasyncMessage.class, "registry")
      .put(ChangeDoiMessage.class, "registry")
      .put(ChecklistNormalizedMessage.class, "checklist")
      .put(ChecklistSyncedMessage.class, "checklist")
      .put(ChecklistAnalyzedMessage.class, "checklist")
      .put(BackboneChangedMessage.class, "checklist")
      .put(MatchDatasetMessage.class, "checklist")
      .build();

    MESSAGE_TO_ROUTING_KEY_MAPPING = ImmutableMap.<Class<? extends Message>, String>builder()
      .put(AbcdaDownloadFinishedMessage.class, AbcdaDownloadFinishedMessage.ROUTING_KEY)
      .put(CrawlErrorMessage.class, "crawl.error")
      .put(CrawlFinishedMessage.class, "crawl.finished")
      .put(CrawlStartedMessage.class, "crawl.started")
      .put(CrawlRequestMessage.class, "crawl.request")
      .put(CrawlResponseMessage.class, "crawl.response")
      .put(DwcaDownloadFinishedMessage.class, DwcaDownloadFinishedMessage.ROUTING_KEY)
      .put(DwcaMetasyncFinishedMessage.class, DwcaMetasyncFinishedMessage.ROUTING_KEY)
      .put(DwcaValidationFinishedMessage.class, DwcaValidationFinishedMessage.ROUTING_KEY)
      .put(OccurrenceFragmentedMessage.class, "crawler.fragment.new")
      .put(FragmentPersistedMessage.class, "occurrence.fragment.persisted")
      .put(VerbatimPersistedMessage.class, "occurrence.verbatim.persisted")
      .put(OccurrenceMutatedMessage.class, "occurrence.interpreted.mutated")
      .put(DeleteOccurrenceMessage.class, "occurrence.delete.occurrence")
      .put(DeleteDataResourceOccurrencesMessage.class, "occurrence.delete.dataresource")
      .put(DeleteDatasetOccurrencesMessage.class, "occurrence.delete.dataset")
      .put(ParseFragmentMessage.class, "occurrence.parse.occurrence")
      .put(ParseDatasetMessage.class, "occurrence.parse.dataset")
      .put(InterpretVerbatimMessage.class, "occurrence.interpret.occurrence")
      .put(InterpretDatasetMessage.class, "occurrence.interpret.dataset")
      .put(IndexDatasetMessage.class, "occurrence.index.dataset")
      .put(RegistryChangeMessage.class, "registry.change.#")
      .put(StartCrawlMessage.class, "crawl.start")
      .put(StartMetasyncMessage.class, StartMetasyncMessage.ROUTING_KEY)
      .put(ChecklistNormalizedMessage.class, ChecklistNormalizedMessage.ROUTING_KEY)
      .put(ChecklistSyncedMessage.class, ChecklistSyncedMessage.ROUTING_KEY)
      .put(ChecklistAnalyzedMessage.class, ChecklistAnalyzedMessage.ROUTING_KEY)
      .put(BackboneChangedMessage.class, BackboneChangedMessage.ROUTING_KEY)
      .put(ChangeDoiMessage.class, ChangeDoiMessage.ROUTING_KEY)
      .put(MatchDatasetMessage.class, MatchDatasetMessage.ROUTING_KEY)
      .put(ExtendedRecordAvailableMessage.class,ExtendedRecordAvailableMessage.ROUTING_KEY)
      .build();
  }

  public DefaultMessageRegistry() {
    exchangeMapping.putAll(MESSAGE_TO_EXCHANGE_MAPPING);
    routingKeyMapping.putAll(MESSAGE_TO_ROUTING_KEY_MAPPING);
  }

  @Override
  public Optional<String> getExchange(Class<? extends Message> message) {
    checkNotNull(message, "message can't be null");

    return Optional.fromNullable(exchangeMapping.get(message));
  }

  @Override
  public Optional<String> getGenericRoutingKey(Class<? extends Message> message) {
    checkNotNull(message, "message can't be null");

    return Optional.fromNullable(routingKeyMapping.get(message));
  }

  @Override
  public ImmutableSet<Class<? extends Message>> getRegisteredMessages() {
    synchronized (lock) {
      return ImmutableSet.copyOf(exchangeMapping.keySet());
    }
  }

  /**
   * Used to register a new message or change information about an existing one. No special care is taken to protect
   * the default set of messages.
   *
   * @param message    to register
   * @param exchange   it uses
   * @param routingKey it uses
   */
  @Override
  public void register(Class<? extends Message> message, String exchange, String routingKey) {
    checkNotNull(message, "message can't be null");
    checkNotNull(exchange, "exchange can't be null");
    checkNotNull(routingKey, "routingKey can't be null");

    synchronized (lock) {
      exchangeMapping.put(message, exchange);
      routingKeyMapping.put(message, routingKey);
    }
  }

  /**
   * Deletes information about a message from the registry. No special care is taken to protect the default set of
   * messages.
   *
   * @param message to unregister
   */
  @Override
  public void unregister(Class<? extends Message> message) {
    checkNotNull(message, "message can't be null");

    synchronized (lock) {
      exchangeMapping.remove(message);
      routingKeyMapping.remove(message);
    }
  }

  /**
   * Deletes information about all message from the registry. No special care is taken to protect the default set of
   * messages.
   */
  @Override
  public void clear() {
    synchronized (lock) {
      exchangeMapping.clear();
      routingKeyMapping.clear();
    }
  }
}
