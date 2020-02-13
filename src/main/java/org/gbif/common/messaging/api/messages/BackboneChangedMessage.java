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

import org.gbif.api.model.checklistbank.DatasetMetrics;
import org.gbif.common.messaging.api.Message;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

/** The message sent whenever the GBIF backbone has been altered. */
public class BackboneChangedMessage implements Message {
  public static final String ROUTING_KEY = "backbone.changed";

  private final DatasetMetrics metrics;

  @JsonCreator
  public BackboneChangedMessage(@JsonProperty("metrics") DatasetMetrics metrics) {
    this.metrics = Preconditions.checkNotNull(metrics);
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }

  public DatasetMetrics getMetrics() {
    return metrics;
  }

  @Override
  public int hashCode() {
    return metrics.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final BackboneChangedMessage other = (BackboneChangedMessage) obj;
    return Objects.equals(this.metrics, other.metrics);
  }
}
