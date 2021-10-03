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

import org.gbif.api.model.common.DOI;
import org.gbif.api.model.common.DoiStatus;
import org.gbif.common.messaging.api.Message;

import java.net.URI;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A message to request an update to a DOIs metadata and target URL in DataCite. The DOI can be in
 * any current state (registered, reserved, deleted) or even yet unknown to DataCite.
 */
public class ChangeDoiMessage implements Message {
  public static final String ROUTING_KEY = "doi.change";

  public static final String DOI_FIELD = "doi";
  public static final String DOI_STATUS_FIELD = "status";
  public static final String METADATA_FIELD = "metadata";
  public static final String TARGET_FIELD = "target";

  private final DOI doi;
  private final DoiStatus status;
  private final String metadata;
  private final URI target;

  @JsonCreator
  public ChangeDoiMessage(
      @JsonProperty("status") DoiStatus status,
      @JsonProperty("doi") DOI doi,
      @JsonProperty("metadata") String metadata,
      @JsonProperty("target") URI target) {
    this.status = Objects.requireNonNull(status, "status can't be null");
    this.doi = Objects.requireNonNull(doi, "doi can't be null");
    if (status != DoiStatus.DELETED) {
      Objects.requireNonNull(metadata, "metadata can't be null");
    }
    this.metadata = metadata;
    if (status == DoiStatus.REGISTERED) {
      Objects.requireNonNull(target, "target URI can't be null");
    }
    this.target = target;
  }

  public DOI getDoi() {
    return doi;
  }

  /** @return the desired status this doi should be updated to */
  public DoiStatus getStatus() {
    return status;
  }

  /** @return the metadata as datacite xml */
  public String getMetadata() {
    return metadata;
  }

  public URI getTarget() {
    return target;
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }

  @Override
  public int hashCode() {
    return Objects.hash(doi, status, metadata, target);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final ChangeDoiMessage other = (ChangeDoiMessage) obj;
    return Objects.equals(this.doi, other.doi)
        && Objects.equals(this.status, other.status)
        && Objects.equals(this.metadata, other.metadata)
        && Objects.equals(this.target, other.target);
  }
}
