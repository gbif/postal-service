/*
 * Copyright 2021 Global Biodiversity Information Facility (GBIF)
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
package org.gbif.common.messaging.api.messages;

import org.gbif.api.model.crawler.DwcaValidationReport;
import org.gbif.api.vocabulary.EndpointType;
import org.gbif.api.vocabulary.OccurrenceSchemaType;

import java.util.Arrays;
import java.util.UUID;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * We send this message for every new occurrence fragment we produce. This class does not make a
 * copy of the fragment at the moment so make sure that you don't change the underlying byte array.
 */
public class OccurrenceFragmentedMessage implements DatasetBasedMessage {

  private final UUID datasetUuid;

  private final int attempt;

  private final byte[] fragment;

  private final OccurrenceSchemaType schemaType;

  private final EndpointType endpointType;

  private final DwcaValidationReport validationReport;

  @JsonCreator
  public OccurrenceFragmentedMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("attempt") int attempt,
      @JsonProperty("fragment") byte[] fragment,
      @JsonProperty("schemaType") OccurrenceSchemaType schemaType,
      @JsonProperty("endpointType") EndpointType endpointType,
      @Nullable @JsonProperty("validationReport") DwcaValidationReport validationReport) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    checkArgument(attempt > 0, "attempt must be greater than 0");
    this.attempt = attempt;
    this.fragment = checkNotNull(fragment, "fragment can't be null");
    this.schemaType = checkNotNull(schemaType, "schemaType can't be null");
    this.endpointType = checkNotNull(endpointType, "endpointType can't be null");
    this.validationReport = validationReport;
  }

  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  public int getAttempt() {
    return attempt;
  }

  public byte[] getFragment() {
    return fragment;
  }

  public OccurrenceSchemaType getSchemaType() {
    return schemaType;
  }

  public EndpointType getEndpointType() {
    return endpointType;
  }

  public DwcaValidationReport getValidationReport() {
    return validationReport;
  }

  @Override
  public String getRoutingKey() {
    // TODO
    return "crawler.fragment.new";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof OccurrenceFragmentedMessage)) {
      return false;
    }

    final OccurrenceFragmentedMessage other = (OccurrenceFragmentedMessage) obj;
    return Objects.equal(this.datasetUuid, other.datasetUuid)
        && Objects.equal(this.attempt, other.attempt)
        && Arrays.equals(this.fragment, other.fragment)
        && Objects.equal(this.schemaType, other.schemaType)
        && Objects.equal(this.endpointType, other.endpointType)
        && Objects.equal(this.validationReport, other.validationReport);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(
        datasetUuid,
        attempt,
        Arrays.hashCode(fragment),
        schemaType,
        endpointType,
        validationReport);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("datasetUuid", datasetUuid)
        .add("attempt", attempt)
        .add("fragment", fragment)
        .add("schemaType", schemaType)
        .add("endpointType", endpointType)
        .add("validationReport", validationReport)
        .toString();
  }
}
