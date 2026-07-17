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
package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.ExchangeType;
import org.gbif.common.messaging.MessageBinding;
import org.gbif.utils.PreconditionUtils;

import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@MessageBinding(exchange = ExchangeType.CRAWLER, routingKey = DwcDpMetadataSyncFinishedMessage.ROUTING_KEY)
public class DwcDpMetadataSyncFinishedMessage implements DatasetBasedMessage {

  public static final String ROUTING_KEY = "crawl.dwcdp.metadatasync.finished";

  private final UUID datasetUuid;
  private final int attempt;
  private final boolean containsOccurrences;
  private final boolean containsEvents;

  @Deprecated
  public DwcDpMetadataSyncFinishedMessage(UUID datasetUuid, int attempt) {
    this(datasetUuid, attempt, null, null);
  }

  @JsonCreator
  public DwcDpMetadataSyncFinishedMessage(
    @JsonProperty("datasetUuid") UUID datasetUuid,
    @JsonProperty("attempt") int attempt,
    @JsonProperty("containsOccurrences") Boolean containsOccurrences,
    @JsonProperty("containsEvents") Boolean containsEvents) {
    this.datasetUuid = Objects.requireNonNull(datasetUuid, "datasetUuid can't be null");
    PreconditionUtils.checkArgument(attempt > 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    this.containsOccurrences = containsOccurrences != null && containsOccurrences;
    this.containsEvents = containsEvents != null && containsEvents;
  }

  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  public int getAttempt() {
    return attempt;
  }

  public boolean isContainsOccurrences() {
    return containsOccurrences;
  }

  public boolean isContainsEvents() {
    return containsEvents;
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }
}
