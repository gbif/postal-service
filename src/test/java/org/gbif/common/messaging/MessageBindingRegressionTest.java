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
package org.gbif.common.messaging;

import org.gbif.common.messaging.api.Message;
import org.gbif.common.messaging.api.messages.*;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test was created to ensure that all message classes
 * were successfully migrated to use the @MessageBinding annotation.
 * Remove later.
 */
public class MessageBindingRegressionTest {

  private static final Map<Class<? extends Message>, String> EXPECTED_EXCHANGES = Map.<Class<? extends Message>, String>ofEntries(
      Map.entry(AbcdaDownloadFinishedMessage.class, "crawler"),
      Map.entry(CrawlErrorMessage.class, "crawler"),
      Map.entry(CrawlFinishedMessage.class, "crawler"),
      Map.entry(CrawlStartedMessage.class, "crawler"),
      Map.entry(CrawlRequestMessage.class, "crawler"),
      Map.entry(CrawlResponseMessage.class, "crawler"),
      Map.entry(OccurrenceFragmentedMessage.class, "crawler"),
      Map.entry(DwcaDownloadFinishedMessage.class, "crawler"),
      Map.entry(DwcaMetasyncFinishedMessage.class, "crawler"),
      Map.entry(DwcaValidationFinishedMessage.class, "crawler"),
      Map.entry(FragmentPersistedMessage.class, "occurrence"),
      Map.entry(VerbatimPersistedMessage.class, "occurrence"),
      Map.entry(OccurrenceMutatedMessage.class, "occurrence"),
      Map.entry(DeleteOccurrenceMessage.class, "occurrence"),
      Map.entry(DeleteDataResourceOccurrencesMessage.class, "occurrence"),
      Map.entry(DeleteDatasetOccurrencesMessage.class, "occurrence"),
      Map.entry(ParseFragmentMessage.class, "occurrence"),
      Map.entry(ParseDatasetMessage.class, "occurrence"),
      Map.entry(InterpretVerbatimMessage.class, "occurrence"),
      Map.entry(InterpretDatasetMessage.class, "occurrence"),
      Map.entry(RegistryChangeMessage.class, "registry"),
      Map.entry(StartCrawlMessage.class, "registry"),
      Map.entry(StartMetasyncMessage.class, "registry"),
      Map.entry(ChangeDoiMessage.class, "registry"),
      Map.entry(ChecklistNormalizedMessage.class, "checklist"),
      Map.entry(ChecklistSyncedMessage.class, "checklist"),
      Map.entry(ChecklistAnalyzedMessage.class, "checklist"),
      Map.entry(BackboneChangedMessage.class, "checklist"),
      Map.entry(MatchDatasetMessage.class, "checklist"),
      Map.entry(PipelinesXmlMessage.class, "crawler"),
      Map.entry(PipelinesDwcaMessage.class, "crawler"),
      Map.entry(PipelinesAbcdMessage.class, "crawler"),
      Map.entry(PipelinesVerbatimMessage.class, "occurrence"),
      Map.entry(PipelinesInterpretedMessage.class, "occurrence"),
      Map.entry(PipelinesIndexedMessage.class, "occurrence"),
      Map.entry(PipelinesHdfsViewMessage.class, "occurrence"),
      Map.entry(PipelinesBalancerMessage.class, "occurrence"),
      Map.entry(PipelinesFragmenterMessage.class, "occurrence"),
      Map.entry(PipelinesArchiveValidatorMessage.class, "occurrence"),
      Map.entry(PipelinesChecklistValidatorMessage.class, "occurrence"),
      Map.entry(PipelinesMetricsCollectedMessage.class, "occurrence"),
      Map.entry(PipelinesCleanerMessage.class, "occurrence"),
      Map.entry(PipelinesEventsMessage.class, "occurrence"),
      Map.entry(PipelinesEventsInterpretedMessage.class, "occurrence"),
      Map.entry(PipelinesEventsIndexedMessage.class, "occurrence"),
      Map.entry(PipelinesEventsHdfsViewMessage.class, "occurrence"),
      Map.entry(VocabularyReleasedMessage.class, "vocabulary"),
      Map.entry(CamtrapDpDownloadFinishedMessage.class, "crawler"),
      Map.entry(DwcDpDownloadFinishedMessage.class, "crawler"),
      Map.entry(DownloadLauncherMessage.class, "occurrence"),
      Map.entry(DownloadCancelMessage.class, "occurrence"),
      Map.entry(DataWarehouseMessage.class, "occurrence")
  );

  private static final Map<Class<? extends Message>, String> EXPECTED_ROUTING_KEYS = Map.<Class<? extends Message>, String>ofEntries(
      Map.entry(AbcdaDownloadFinishedMessage.class, AbcdaDownloadFinishedMessage.ROUTING_KEY),
      Map.entry(CrawlErrorMessage.class, "crawl.error"),
      Map.entry(CrawlFinishedMessage.class, CrawlFinishedMessage.ROUTING_KEY),
      Map.entry(CrawlStartedMessage.class, "crawl.started"),
      Map.entry(CrawlRequestMessage.class, "crawl.request"),
      Map.entry(CrawlResponseMessage.class, "crawl.response"),
      Map.entry(OccurrenceFragmentedMessage.class, "crawler.fragment.new"),
      Map.entry(DwcaDownloadFinishedMessage.class, DwcaDownloadFinishedMessage.ROUTING_KEY),
      Map.entry(DwcaMetasyncFinishedMessage.class, DwcaMetasyncFinishedMessage.ROUTING_KEY),
      Map.entry(DwcaValidationFinishedMessage.class, DwcaValidationFinishedMessage.ROUTING_KEY),
      Map.entry(FragmentPersistedMessage.class, "occurrence.fragment.persisted"),
      Map.entry(VerbatimPersistedMessage.class, "occurrence.verbatim.persisted"),
      Map.entry(OccurrenceMutatedMessage.class, "occurrence.interpreted.mutated"),
      Map.entry(DeleteOccurrenceMessage.class, "occurrence.delete.occurrence"),
      Map.entry(DeleteDataResourceOccurrencesMessage.class, "occurrence.delete.dataresource"),
      Map.entry(DeleteDatasetOccurrencesMessage.class, "occurrence.delete.dataset"),
      Map.entry(ParseFragmentMessage.class, "occurrence.parse.occurrence"),
      Map.entry(ParseDatasetMessage.class, "occurrence.parse.dataset"),
      Map.entry(InterpretVerbatimMessage.class, "occurrence.interpret.occurrence"),
      Map.entry(InterpretDatasetMessage.class, "occurrence.interpret.dataset"),
      Map.entry(RegistryChangeMessage.class, "registry.change.#"),
      Map.entry(StartCrawlMessage.class, "crawl.start"),
      Map.entry(StartMetasyncMessage.class, StartMetasyncMessage.ROUTING_KEY),
      Map.entry(ChangeDoiMessage.class, ChangeDoiMessage.ROUTING_KEY),
      Map.entry(ChecklistNormalizedMessage.class, ChecklistNormalizedMessage.ROUTING_KEY),
      Map.entry(ChecklistSyncedMessage.class, ChecklistSyncedMessage.ROUTING_KEY),
      Map.entry(ChecklistAnalyzedMessage.class, ChecklistAnalyzedMessage.ROUTING_KEY),
      Map.entry(BackboneChangedMessage.class, BackboneChangedMessage.ROUTING_KEY),
      Map.entry(MatchDatasetMessage.class, MatchDatasetMessage.ROUTING_KEY),
      Map.entry(PipelinesXmlMessage.class, PipelinesXmlMessage.ROUTING_KEY),
      Map.entry(PipelinesDwcaMessage.class, PipelinesDwcaMessage.ROUTING_KEY),
      Map.entry(PipelinesAbcdMessage.class, PipelinesAbcdMessage.ROUTING_KEY),
      Map.entry(PipelinesVerbatimMessage.class, PipelinesVerbatimMessage.ROUTING_KEY),
      Map.entry(PipelinesInterpretedMessage.class, PipelinesInterpretedMessage.ROUTING_KEY),
      Map.entry(PipelinesIndexedMessage.class, PipelinesIndexedMessage.ROUTING_KEY),
      Map.entry(PipelinesHdfsViewMessage.class, PipelinesHdfsViewMessage.ROUTING_KEY),
      Map.entry(PipelinesBalancerMessage.class, PipelinesBalancerMessage.ROUTING_KEY),
      Map.entry(PipelinesFragmenterMessage.class, PipelinesFragmenterMessage.ROUTING_KEY),
      Map.entry(PipelinesArchiveValidatorMessage.class, PipelinesArchiveValidatorMessage.ROUTING_KEY),
      Map.entry(PipelinesChecklistValidatorMessage.class, PipelinesChecklistValidatorMessage.ROUTING_KEY),
      Map.entry(PipelinesMetricsCollectedMessage.class, PipelinesMetricsCollectedMessage.ROUTING_KEY),
      Map.entry(PipelinesCleanerMessage.class, PipelinesCleanerMessage.ROUTING_KEY),
      Map.entry(PipelinesEventsMessage.class, PipelinesEventsMessage.ROUTING_KEY),
      Map.entry(PipelinesEventsInterpretedMessage.class, PipelinesEventsInterpretedMessage.ROUTING_KEY),
      Map.entry(PipelinesEventsIndexedMessage.class, PipelinesEventsIndexedMessage.ROUTING_KEY),
      Map.entry(PipelinesEventsHdfsViewMessage.class, PipelinesEventsHdfsViewMessage.ROUTING_KEY),
      Map.entry(VocabularyReleasedMessage.class, VocabularyReleasedMessage.ROUTING_KEY),
      Map.entry(CamtrapDpDownloadFinishedMessage.class, CamtrapDpDownloadFinishedMessage.ROUTING_KEY),
      Map.entry(DwcDpDownloadFinishedMessage.class, DwcDpDownloadFinishedMessage.ROUTING_KEY),
      Map.entry(DownloadLauncherMessage.class, DownloadLauncherMessage.ROUTING_KEY),
      Map.entry(DownloadCancelMessage.class, DownloadCancelMessage.ROUTING_KEY),
      Map.entry(DataWarehouseMessage.class, DataWarehouseMessage.ROUTING_KEY)
  );

  private final DefaultMessageRegistry registry = new DefaultMessageRegistry();

  @Disabled
  @Test
  public void allExpectedMessagesAreRegistered() {
    Set<Class<? extends Message>> registered = registry.getRegisteredMessages();
    EXPECTED_EXCHANGES.keySet().forEach(messageClass ->
        assertTrue(
            registered.contains(messageClass),
            messageClass.getSimpleName() + " is not registered"
        )
    );
  }

  @Disabled
  @Test
  public void noUnexpectedMessagesAreRegistered() {
    assertEquals(
        EXPECTED_EXCHANGES.keySet(),
        registry.getRegisteredMessages(),
        "Registered messages do not match expected set"
    );
  }

  @Disabled
  @Test
  public void exchangesMatchExpected() {
    assertAll(
        EXPECTED_EXCHANGES.entrySet().stream().map(entry ->
            () -> assertEquals(
                entry.getValue(),
                registry.getExchange(entry.getKey()).orElse(null),
                "Wrong exchange for " + entry.getKey().getSimpleName()
            )
        )
    );
  }

  @Disabled
  @Test
  public void routingKeysMatchExpected() {
    EXPECTED_ROUTING_KEYS.forEach((messageClass, expectedKey) ->
        assertEquals(
            expectedKey,
            registry.getGenericRoutingKey(messageClass).orElse(null),
            "Wrong routing key for " + messageClass.getSimpleName()
        )
    );
  }

  @Disabled
  @Test
  public void allMessageClassesHaveAnnotation() {
    Reflections reflections = new Reflections("org.gbif.common.messaging.api.messages");
    reflections.getSubTypesOf(Message.class)
        .stream()
        .filter(cls -> !cls.isInterface())
        .forEach(cls ->
        assertTrue(
            cls.isAnnotationPresent(MessageBinding.class),
            cls.getSimpleName() + " is missing @MessageBinding"
        )
    );
  }
}
