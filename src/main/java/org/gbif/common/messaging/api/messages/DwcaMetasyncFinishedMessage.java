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
package org.gbif.common.messaging.api.messages;

import org.gbif.api.model.crawler.DwcaValidationReport;
import org.gbif.api.vocabulary.DatasetType;
import org.gbif.utils.PreconditionUtils;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * We send this every time the dataset metadata found in a darwin core archive has been deposited in
 * the metadata repository. This includes updating the registered dataset metadata and synchronizing
 * potentially all found dataset constituents, e.g. GSDs in CoL.
 */
public class DwcaMetasyncFinishedMessage implements DatasetBasedMessage {

  public static final String ROUTING_KEY = "crawl.dwca.metasync.finished";

  private final UUID datasetUuid;
  private final DatasetType datasetType;
  private final URI source;
  private final int attempt;
  private final Map<String, UUID> constituents;
  private final DwcaValidationReport validationReport;
  private final Platform platform;

  @JsonCreator
  public DwcaMetasyncFinishedMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("datasetType") DatasetType datasetType,
      @JsonProperty("source") URI source,
      @JsonProperty("attempt") int attempt,
      @JsonProperty("constituents") Map<String, UUID> constituents,
      @JsonProperty("validationReport") DwcaValidationReport validationReport,
      @JsonProperty("platform") Platform platform) {
    this.datasetUuid = Objects.requireNonNull(datasetUuid, "datasetUuid can't be null");
    this.datasetType = Objects.requireNonNull(datasetType, "datasetType can't be null");
    this.source = Objects.requireNonNull(source, "source can't be null");
    PreconditionUtils.checkArgument(attempt > 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    this.constituents = Objects.requireNonNull(constituents, "constituents can't be null");
    this.validationReport = Objects.requireNonNull(validationReport, "validationReport can't be null");
    this.platform = platform != null ? platform : Platform.ALL;
  }

  /** @return dataset uuid */
  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  /** @return the dataset type as given in the registry */
  public DatasetType getDatasetType() {
    return datasetType;
  }

  /** @return source the archive has been downloaded from */
  public URI getSource() {
    return source;
  }

  public int getAttempt() {
    return attempt;
  }

  /**
   * Defines a mapping of the dwc:datasetID used in the archive data and our UUID datasetUuid for
   * all constituent (aka sub-) datasets found in this archive. Its a convenience map needed for
   * indexing but could also be build by querying the registry constituent datasets and their tags.
   *
   * @return map of dwc:datasetID to datasetUuid
   */
  public Map<String, UUID> getConstituents() {
    return constituents;
  }

  /** @return the validationReport for this archive */
  public DwcaValidationReport getValidationReport() {
    return validationReport;
  }

  /** @return the platform that must index the archive */
  public Platform getPlatform() {
    return platform;
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DwcaMetasyncFinishedMessage that = (DwcaMetasyncFinishedMessage) o;
    return attempt == that.attempt
        && Objects.equals(datasetUuid, that.datasetUuid)
        && datasetType == that.datasetType
        && Objects.equals(source, that.source)
        && Objects.equals(constituents, that.constituents)
        && Objects.equals(validationReport, that.validationReport)
        && platform == that.platform;
  }

  @Override
  public int hashCode() {
    return Objects.hash(datasetUuid, datasetType, source, attempt, constituents, validationReport, platform);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", DwcaMetasyncFinishedMessage.class.getSimpleName() + "[", "]")
        .add("datasetUuid=" + datasetUuid)
        .add("datasetType=" + datasetType)
        .add("source=" + source)
        .add("attempt=" + attempt)
        .add("constituents=" + constituents)
        .add("validationReport=" + validationReport)
        .add("platform=" + platform)
        .toString();
  }
}
