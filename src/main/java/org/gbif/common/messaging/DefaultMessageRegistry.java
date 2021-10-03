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
package org.gbif.common.messaging;

import org.gbif.common.messaging.api.Message;
import org.gbif.common.messaging.api.MessageRegistry;
import org.gbif.common.messaging.api.messages.AbcdaDownloadFinishedMessage;
import org.gbif.common.messaging.api.messages.BackboneChangedMessage;
import org.gbif.common.messaging.api.messages.ChangeDoiMessage;
import org.gbif.common.messaging.api.messages.ChecklistAnalyzedMessage;
import org.gbif.common.messaging.api.messages.ChecklistNormalizedMessage;
import org.gbif.common.messaging.api.messages.ChecklistSyncedMessage;
import org.gbif.common.messaging.api.messages.CrawlErrorMessage;
import org.gbif.common.messaging.api.messages.CrawlFinishedMessage;
import org.gbif.common.messaging.api.messages.CrawlRequestMessage;
import org.gbif.common.messaging.api.messages.CrawlResponseMessage;
import org.gbif.common.messaging.api.messages.CrawlStartedMessage;
import org.gbif.common.messaging.api.messages.DeleteDataResourceOccurrencesMessage;
import org.gbif.common.messaging.api.messages.DeleteDatasetOccurrencesMessage;
import org.gbif.common.messaging.api.messages.DeleteOccurrenceMessage;
import org.gbif.common.messaging.api.messages.DwcaDownloadFinishedMessage;
import org.gbif.common.messaging.api.messages.DwcaMetasyncFinishedMessage;
import org.gbif.common.messaging.api.messages.DwcaValidationFinishedMessage;
import org.gbif.common.messaging.api.messages.FragmentPersistedMessage;
import org.gbif.common.messaging.api.messages.InterpretDatasetMessage;
import org.gbif.common.messaging.api.messages.InterpretVerbatimMessage;
import org.gbif.common.messaging.api.messages.MatchDatasetMessage;
import org.gbif.common.messaging.api.messages.OccurrenceFragmentedMessage;
import org.gbif.common.messaging.api.messages.OccurrenceMutatedMessage;
import org.gbif.common.messaging.api.messages.ParseDatasetMessage;
import org.gbif.common.messaging.api.messages.ParseFragmentMessage;
import org.gbif.common.messaging.api.messages.PipelinesAbcdMessage;
import org.gbif.common.messaging.api.messages.PipelinesBalancerMessage;
import org.gbif.common.messaging.api.messages.PipelinesDwcaMessage;
import org.gbif.common.messaging.api.messages.PipelinesFragmenterMessage;
import org.gbif.common.messaging.api.messages.PipelinesHdfsViewBuiltMessage;
import org.gbif.common.messaging.api.messages.PipelinesIndexedMessage;
import org.gbif.common.messaging.api.messages.PipelinesInterpretedMessage;
import org.gbif.common.messaging.api.messages.PipelinesVerbatimMessage;
import org.gbif.common.messaging.api.messages.PipelinesXmlMessage;
import org.gbif.common.messaging.api.messages.RegistryChangeMessage;
import org.gbif.common.messaging.api.messages.StartCrawlMessage;
import org.gbif.common.messaging.api.messages.StartMetasyncMessage;
import org.gbif.common.messaging.api.messages.VerbatimPersistedMessage;
import org.gbif.common.messaging.api.messages.VocabularyReleasedMessage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

/**
 * A default implementation of the {@link MessageRegistry} interface where all the messages from
 * this projects {@code org.gbif.common.messaging.api} package are already preregistered.
 *
 * <p>This class is thread-safe.
 */
@ThreadSafe
public class DefaultMessageRegistry implements MessageRegistry {

  private final Object lock = new Object();

  /* These are the default messages which are the seed for any new instance */
  private static final Map<Class<? extends Message>, String> MESSAGE_TO_EXCHANGE_MAPPING;
  private static final Map<Class<? extends Message>, String> MESSAGE_TO_ROUTING_KEY_MAPPING;

  @GuardedBy("lock")
  private final ConcurrentMap<Class<? extends Message>, String> exchangeMapping =
      new ConcurrentHashMap<>();

  @GuardedBy("lock")
  private final ConcurrentMap<Class<? extends Message>, String> routingKeyMapping =
      new ConcurrentHashMap<>();

  static {
    Map<Class<? extends Message>, String> messageToExchangeMappingInternal = new HashMap<>();
    messageToExchangeMappingInternal.put(AbcdaDownloadFinishedMessage.class, "crawler");
    messageToExchangeMappingInternal.put(CrawlErrorMessage.class, "crawler");
    messageToExchangeMappingInternal.put(CrawlFinishedMessage.class, "crawler");
    messageToExchangeMappingInternal.put(CrawlStartedMessage.class, "crawler");
    messageToExchangeMappingInternal.put(CrawlRequestMessage.class, "crawler");
    messageToExchangeMappingInternal.put(CrawlResponseMessage.class, "crawler");
    messageToExchangeMappingInternal.put(OccurrenceFragmentedMessage.class, "crawler");
    messageToExchangeMappingInternal.put(DwcaDownloadFinishedMessage.class, "crawler");
    messageToExchangeMappingInternal.put(DwcaMetasyncFinishedMessage.class, "crawler");
    messageToExchangeMappingInternal.put(DwcaValidationFinishedMessage.class, "crawler");
    messageToExchangeMappingInternal.put(FragmentPersistedMessage.class, "occurrence");
    messageToExchangeMappingInternal.put(VerbatimPersistedMessage.class, "occurrence");
    messageToExchangeMappingInternal.put(OccurrenceMutatedMessage.class, "occurrence");
    messageToExchangeMappingInternal.put(DeleteOccurrenceMessage.class, "occurrence");
    messageToExchangeMappingInternal.put(DeleteDataResourceOccurrencesMessage.class, "occurrence");
    messageToExchangeMappingInternal.put(DeleteDatasetOccurrencesMessage.class, "occurrence");
    messageToExchangeMappingInternal.put(ParseFragmentMessage.class, "occurrence");
    messageToExchangeMappingInternal.put(ParseDatasetMessage.class, "occurrence");
    messageToExchangeMappingInternal.put(InterpretVerbatimMessage.class, "occurrence");
    messageToExchangeMappingInternal.put(InterpretDatasetMessage.class, "occurrence");
    messageToExchangeMappingInternal.put(RegistryChangeMessage.class, "registry");
    messageToExchangeMappingInternal.put(StartCrawlMessage.class, "registry");
    messageToExchangeMappingInternal.put(StartMetasyncMessage.class, "registry");
    messageToExchangeMappingInternal.put(ChangeDoiMessage.class, "registry");
    messageToExchangeMappingInternal.put(ChecklistNormalizedMessage.class, "checklist");
    messageToExchangeMappingInternal.put(ChecklistSyncedMessage.class, "checklist");
    messageToExchangeMappingInternal.put(ChecklistAnalyzedMessage.class, "checklist");
    messageToExchangeMappingInternal.put(BackboneChangedMessage.class, "checklist");
    messageToExchangeMappingInternal.put(MatchDatasetMessage.class, "checklist");
    messageToExchangeMappingInternal.put(PipelinesXmlMessage.class, "crawler");
    messageToExchangeMappingInternal.put(PipelinesDwcaMessage.class, "crawler");
    messageToExchangeMappingInternal.put(PipelinesAbcdMessage.class, "crawler");
    messageToExchangeMappingInternal.put(PipelinesVerbatimMessage.class, "occurrence");
    messageToExchangeMappingInternal.put(PipelinesInterpretedMessage.class, "occurrence");
    messageToExchangeMappingInternal.put(PipelinesIndexedMessage.class, "occurrence");
    messageToExchangeMappingInternal.put(PipelinesHdfsViewBuiltMessage.class, "occurrence");
    messageToExchangeMappingInternal.put(PipelinesBalancerMessage.class, "occurrence");
    messageToExchangeMappingInternal.put(PipelinesFragmenterMessage.class, "occurrence");
    messageToExchangeMappingInternal.put(VocabularyReleasedMessage.class, "vocabulary");
    MESSAGE_TO_EXCHANGE_MAPPING = Collections.unmodifiableMap(messageToExchangeMappingInternal);

    Map<Class<? extends Message>, String> messageToRoutingKeyMapping = new HashMap<>();
    messageToRoutingKeyMapping.put(AbcdaDownloadFinishedMessage.class, AbcdaDownloadFinishedMessage.ROUTING_KEY);
    messageToRoutingKeyMapping.put(CrawlErrorMessage.class, "crawl.error");
    messageToRoutingKeyMapping.put(CrawlFinishedMessage.class, CrawlFinishedMessage.ROUTING_KEY);
    messageToRoutingKeyMapping.put(CrawlStartedMessage.class, "crawl.started");
    messageToRoutingKeyMapping.put(CrawlRequestMessage.class, "crawl.request");
    messageToRoutingKeyMapping.put(CrawlResponseMessage.class, "crawl.response");
    messageToRoutingKeyMapping.put(DwcaDownloadFinishedMessage.class, DwcaDownloadFinishedMessage.ROUTING_KEY);
    messageToRoutingKeyMapping.put(DwcaMetasyncFinishedMessage.class, DwcaMetasyncFinishedMessage.ROUTING_KEY);
    messageToRoutingKeyMapping.put(DwcaValidationFinishedMessage.class, DwcaValidationFinishedMessage.ROUTING_KEY);
    messageToRoutingKeyMapping.put(OccurrenceFragmentedMessage.class, "crawler.fragment.new");
    messageToRoutingKeyMapping.put(FragmentPersistedMessage.class, "occurrence.fragment.persisted");
    messageToRoutingKeyMapping.put(VerbatimPersistedMessage.class, "occurrence.verbatim.persisted");
    messageToRoutingKeyMapping.put(OccurrenceMutatedMessage.class, "occurrence.interpreted.mutated");
    messageToRoutingKeyMapping.put(DeleteOccurrenceMessage.class, "occurrence.delete.occurrence");
    messageToRoutingKeyMapping.put(DeleteDataResourceOccurrencesMessage.class, "occurrence.delete.dataresource");
    messageToRoutingKeyMapping.put(DeleteDatasetOccurrencesMessage.class, "occurrence.delete.dataset");
    messageToRoutingKeyMapping.put(ParseFragmentMessage.class, "occurrence.parse.occurrence");
    messageToRoutingKeyMapping.put(ParseDatasetMessage.class, "occurrence.parse.dataset");
    messageToRoutingKeyMapping.put(InterpretVerbatimMessage.class, "occurrence.interpret.occurrence");
    messageToRoutingKeyMapping.put(InterpretDatasetMessage.class, "occurrence.interpret.dataset");
    messageToRoutingKeyMapping.put(RegistryChangeMessage.class, "registry.change.#");
    messageToRoutingKeyMapping.put(StartCrawlMessage.class, "crawl.start");
    messageToRoutingKeyMapping.put(StartMetasyncMessage.class, StartMetasyncMessage.ROUTING_KEY);
    messageToRoutingKeyMapping.put(ChecklistNormalizedMessage.class, ChecklistNormalizedMessage.ROUTING_KEY);
    messageToRoutingKeyMapping.put(ChecklistSyncedMessage.class, ChecklistSyncedMessage.ROUTING_KEY);
    messageToRoutingKeyMapping.put(ChecklistAnalyzedMessage.class, ChecklistAnalyzedMessage.ROUTING_KEY);
    messageToRoutingKeyMapping.put(BackboneChangedMessage.class, BackboneChangedMessage.ROUTING_KEY);
    messageToRoutingKeyMapping.put(ChangeDoiMessage.class, ChangeDoiMessage.ROUTING_KEY);
    messageToRoutingKeyMapping.put(MatchDatasetMessage.class, MatchDatasetMessage.ROUTING_KEY);
    messageToRoutingKeyMapping.put(PipelinesDwcaMessage.class, PipelinesDwcaMessage.ROUTING_KEY);
    messageToRoutingKeyMapping.put(PipelinesXmlMessage.class, PipelinesXmlMessage.ROUTING_KEY);
    messageToRoutingKeyMapping.put(PipelinesAbcdMessage.class, PipelinesAbcdMessage.ROUTING_KEY);
    messageToRoutingKeyMapping.put(PipelinesVerbatimMessage.class, PipelinesVerbatimMessage.ROUTING_KEY);
    messageToRoutingKeyMapping.put(PipelinesInterpretedMessage.class, PipelinesInterpretedMessage.ROUTING_KEY);
    messageToRoutingKeyMapping.put(PipelinesIndexedMessage.class, PipelinesIndexedMessage.ROUTING_KEY);
    messageToRoutingKeyMapping.put(PipelinesHdfsViewBuiltMessage.class, PipelinesHdfsViewBuiltMessage.ROUTING_KEY);
    messageToRoutingKeyMapping.put(PipelinesBalancerMessage.class, PipelinesBalancerMessage.ROUTING_KEY);
    messageToRoutingKeyMapping.put(PipelinesFragmenterMessage.class, PipelinesFragmenterMessage.ROUTING_KEY);
    messageToRoutingKeyMapping.put(VocabularyReleasedMessage.class, VocabularyReleasedMessage.ROUTING_KEY);
    MESSAGE_TO_ROUTING_KEY_MAPPING = Collections.unmodifiableMap(messageToRoutingKeyMapping);
  }

  public DefaultMessageRegistry() {
    exchangeMapping.putAll(MESSAGE_TO_EXCHANGE_MAPPING);
    routingKeyMapping.putAll(MESSAGE_TO_ROUTING_KEY_MAPPING);
  }

  @Override
  @GuardedBy("lock")
  public Optional<String> getExchange(Class<? extends Message> message) {
    Objects.requireNonNull(message, "message can't be null");

    return Optional.ofNullable(exchangeMapping.get(message));
  }

  @GuardedBy("lock")
  @Override
  public Optional<String> getGenericRoutingKey(Class<? extends Message> message) {
    Objects.requireNonNull(message, "message can't be null");

    return Optional.ofNullable(routingKeyMapping.get(message));
  }

  @Override
  public Set<Class<? extends Message>> getRegisteredMessages() {
    synchronized (lock) {
      return Collections.unmodifiableSet(exchangeMapping.keySet());
    }
  }

  /**
   * Used to register a new message or change information about an existing one. No special care is
   * taken to protect the default set of messages.
   *
   * @param message to register
   * @param exchange it uses
   * @param routingKey it uses
   */
  @Override
  public void register(Class<? extends Message> message, String exchange, String routingKey) {
    Objects.requireNonNull(message, "message can't be null");
    Objects.requireNonNull(exchange, "exchange can't be null");
    Objects.requireNonNull(routingKey, "routingKey can't be null");

    synchronized (lock) {
      exchangeMapping.put(message, exchange);
      routingKeyMapping.put(message, routingKey);
    }
  }

  /**
   * Deletes information about a message from the registry. No special care is taken to protect the
   * default set of messages.
   *
   * @param message to unregister
   */
  @Override
  public void unregister(Class<? extends Message> message) {
    Objects.requireNonNull(message, "message can't be null");

    synchronized (lock) {
      exchangeMapping.remove(message);
      routingKeyMapping.remove(message);
    }
  }

  /**
   * Deletes information about all message from the registry. No special care is taken to protect
   * the default set of messages.
   */
  @Override
  public void clear() {
    synchronized (lock) {
      exchangeMapping.clear();
      routingKeyMapping.clear();
    }
  }
}
