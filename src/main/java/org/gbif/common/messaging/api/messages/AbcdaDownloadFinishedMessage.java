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

import org.gbif.api.vocabulary.EndpointType;

import java.net.URI;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * We send this every time an ABCD archive has been downloaded. This includes cases when the archive
 * hasn't been modified since we last downloaded it.
 */
public class AbcdaDownloadFinishedMessage implements DatasetBasedMessage {

  public static final String ROUTING_KEY = "crawl.abcda.download.finished";

  private final UUID datasetUuid;
  private final URI source;
  private final int attempt;
  private final Date lastModified;
  private final boolean modified;
  private final EndpointType endpointType;
  private final Platform platform;

  @JsonCreator
  public AbcdaDownloadFinishedMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("source") URI source,
      @JsonProperty("attempt") int attempt,
      @Nullable @JsonProperty("lastModified") Date lastModified,
      @JsonProperty("modified") boolean modified,
      @JsonProperty("endpointType") EndpointType endpointType,
      @JsonProperty("platform") Platform platform) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    this.source = checkNotNull(source, "source can't be null");
    checkArgument(attempt > 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    this.lastModified = lastModified;
    this.modified = modified;
    this.endpointType = endpointType;
    this.platform = platform != null ? platform : Platform.ALL;
  }

  /** @return dataset uuid */
  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  /** @return source the archive has been downloaded from */
  public URI getSource() {
    return source;
  }

  public int getAttempt() {
    return attempt;
  }

  public EndpointType getEndpointType() {
    return endpointType;
  }

  /** @return the date the downloaded archive was last modified or null e.g. for failed downloads */
  @Nullable
  public Date getLastModified() {
    return lastModified;
  }

  /**
   * @return true if the archive has changed since we last downloaded it or never been downloaded
   *     before
   */
  public boolean isModified() {
    return modified;
  }

  /** @return platform that must index the Abcd fragment */
  public Platform getPlatform() {
    return platform;
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof AbcdaDownloadFinishedMessage)) {
      return false;
    }

    AbcdaDownloadFinishedMessage other = (AbcdaDownloadFinishedMessage) obj;
    return Objects.equal(this.datasetUuid, other.datasetUuid)
        && Objects.equal(this.source, other.source)
        && Objects.equal(this.attempt, other.attempt)
        && Objects.equal(this.lastModified, other.lastModified)
        && Objects.equal(this.endpointType, other.endpointType)
        && Objects.equal(this.modified, other.modified);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(datasetUuid, source, attempt, lastModified, modified);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("datasetUuid", datasetUuid)
        .add("source", source)
        .add("attempt", attempt)
        .add("lastModified", lastModified)
        .add("modified", modified)
        .add("endpointType", endpointType)
        .toString();
  }
}
