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
import org.gbif.api.vocabulary.EndpointType;
import org.gbif.utils.PreconditionUtils;

import java.net.URI;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * We send this every time a darwin core archive has been validated after being downloaded and its
 * metadata has been synchronized.
 */
public class DwcaValidationFinishedMessage implements DatasetBasedMessage {

  public static final String ROUTING_KEY = "crawl.dwca.validation.finished";

  private final UUID datasetUuid;
  private final DatasetType datasetType;
  private final URI source;
  private final int attempt;
  private final DwcaValidationReport validationReport;
  private final EndpointType endpointType;
  private final Platform platform;

  @JsonCreator
  public DwcaValidationFinishedMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("datasetType") DatasetType datasetType,
      @JsonProperty("source") URI source,
      @JsonProperty("attempt") int attempt,
      @JsonProperty("validationReport") DwcaValidationReport validationReport,
      @JsonProperty("endpointType") EndpointType endpointType,
      @JsonProperty("platform") Platform platform) {
    this.datasetUuid = Objects.requireNonNull(datasetUuid, "datasetUuid can't be null");
    this.datasetType = Objects.requireNonNull(datasetType, "datasetType can't be null");
    this.source = Objects.requireNonNull(source, "source can't be null");
    PreconditionUtils.checkArgument(attempt > 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    this.validationReport = Objects.requireNonNull(validationReport, "validationReport can't be null");
    this.endpointType = endpointType;
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

  /** @return the validationReport for this archive */
  public DwcaValidationReport getValidationReport() {
    return validationReport;
  }

  public EndpointType getEndpointType() {
    return endpointType;
  }

  /** @return the indexing platform that handles the message */
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
    DwcaValidationFinishedMessage that = (DwcaValidationFinishedMessage) o;
    return attempt == that.attempt 
        && Objects.equals(datasetUuid, that.datasetUuid) 
        && datasetType == that.datasetType 
        && Objects.equals(source, that.source) 
        && Objects.equals(validationReport, that.validationReport) 
        && endpointType == that.endpointType 
        && platform == that.platform;
  }

  @Override
  public int hashCode() {
    return Objects.hash(datasetUuid, datasetType, source, attempt, validationReport, endpointType, platform);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", DwcaValidationFinishedMessage.class.getSimpleName() + "[", "]")
        .add("datasetUuid=" + datasetUuid)
        .add("datasetType=" + datasetType)
        .add("source=" + source)
        .add("attempt=" + attempt)
        .add("validationReport=" + validationReport)
        .add("endpointType=" + endpointType)
        .add("platform=" + platform)
        .toString();
  }
}
