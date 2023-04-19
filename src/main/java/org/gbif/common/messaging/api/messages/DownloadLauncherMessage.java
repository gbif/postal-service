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

import lombok.Data;

@Data
public class DownloadLauncherMessage implements Message {

  public static final String ROUTING_KEY = "occurrence.download.launch";

  private final String jobId;

  @JsonCreator
  public DownloadLauncherMessage(@JsonProperty("jobId") String jobId) {
    this.jobId = jobId;
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }
}
