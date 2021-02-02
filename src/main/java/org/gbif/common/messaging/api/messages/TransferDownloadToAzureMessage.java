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

import org.gbif.common.messaging.api.Message;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.google.common.base.Preconditions.checkNotNull;

public class TransferDownloadToAzureMessage implements Message {
  public static final String ROUTING_KEY = "occurrence.download.transfer";

  private final String downloadKey;

  private final String sasToken;
  private final String endpoint;
  private final String containerName;

  @JsonCreator
  public TransferDownloadToAzureMessage(
      @JsonProperty("downloadKey") String downloadKey,
      @JsonProperty("sasToken") String sasToken,
      @JsonProperty("endpoint") String endpoint,
      @JsonProperty("containerName") String containerName) {
    this.downloadKey = checkNotNull(downloadKey, "downloadKey can't be null");
    this.sasToken = checkNotNull(sasToken, "sasToken can't be null");
    this.endpoint = checkNotNull(endpoint, "endpoint can't be null");
    this.containerName = checkNotNull(containerName, "containerName can't be null");
  }

  public String getDownloadKey() {
    return this.downloadKey;
  }

  public String getSasToken() {
    return sasToken;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public String getContainerName() {
    return containerName;
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TransferDownloadToAzureMessage that = (TransferDownloadToAzureMessage) o;
    return downloadKey.equals(that.downloadKey)
        && sasToken.equals(that.sasToken)
        && endpoint.equals(that.endpoint)
        && containerName.equals(that.containerName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(downloadKey, sasToken, endpoint, containerName);
  }

  @Override
  public String toString() {
    return "TransferDownloadToAzureMessage{"
        + "downloadKey='"
        + downloadKey
        + '\''
        + ", sasToken='"
        + sasToken
        + '\''
        + ", endpoint='"
        + endpoint
        + '\''
        + ", containerName='"
        + containerName
        + '\''
        + '}';
  }
}
