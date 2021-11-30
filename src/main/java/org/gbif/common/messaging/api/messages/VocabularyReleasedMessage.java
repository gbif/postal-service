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

import org.gbif.common.messaging.api.Message;
import org.gbif.utils.PreconditionUtils;

import java.net.URI;
import java.util.Objects;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** A message to notify the release of a vocabulary. */
public class VocabularyReleasedMessage implements Message {

  public static final String ROUTING_KEY = "vocabulary.released";

  private String vocabularyName;
  private String version;
  private URI releaseDownloadUrl;

  @JsonCreator
  public VocabularyReleasedMessage(
      @JsonProperty("vocabularyName") String vocabularyName,
      @JsonProperty("version") String version,
      @JsonProperty("releaseDownloadUrl") URI releaseDownloadUrl) {
    PreconditionUtils.checkArgument(
        StringUtils.isNotEmpty(vocabularyName), "vocabulary name can't be null");
    PreconditionUtils.checkArgument(StringUtils.isNotEmpty(version), "version can't be null");
    this.vocabularyName = vocabularyName;
    this.version = version;
    this.releaseDownloadUrl =
        Objects.requireNonNull(releaseDownloadUrl, "release download URL can't be null");
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }

  public String getVocabularyName() {
    return vocabularyName;
  }

  public void setVocabularyName(String vocabularyName) {
    this.vocabularyName = vocabularyName;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public URI getReleaseDownloadUrl() {
    return releaseDownloadUrl;
  }

  public void setReleaseDownloadUrl(URI releaseDownloadUrl) {
    this.releaseDownloadUrl = releaseDownloadUrl;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VocabularyReleasedMessage that = (VocabularyReleasedMessage) o;
    return Objects.equals(vocabularyName, that.vocabularyName)
        && Objects.equals(version, that.version)
        && Objects.equals(releaseDownloadUrl, that.releaseDownloadUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(vocabularyName, version, releaseDownloadUrl);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", VocabularyReleasedMessage.class.getSimpleName() + "[", "]")
        .add("vocabularyName='" + vocabularyName + "'")
        .add("version='" + version + "'")
        .add("releaseDownloadUrl=" + releaseDownloadUrl)
        .toString();
  }
}
