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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * This message instructs the processors to parse an occurrence's original fragment into a
 * VerbatimOccurrence.
 */
public class ParseFragmentMessage implements Message {

  private final long occurrenceKey;

  @JsonCreator
  public ParseFragmentMessage(@JsonProperty("occurrenceKey") long occurrenceKey) {
    checkArgument(occurrenceKey > 0, "occurrenceKey must be greater than 0");
    this.occurrenceKey = occurrenceKey;
  }

  public long getOccurrenceKey() {
    return occurrenceKey;
  }

  @Override
  public String getRoutingKey() {
    return "occurrence.parse.occurrence";
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(occurrenceKey);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final ParseFragmentMessage other = (ParseFragmentMessage) obj;
    return Objects.equal(this.occurrenceKey, other.occurrenceKey);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("occurrenceKey", occurrenceKey).toString();
  }
}
