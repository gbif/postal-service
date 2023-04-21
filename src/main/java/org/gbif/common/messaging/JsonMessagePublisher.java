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
package org.gbif.common.messaging;

import org.gbif.common.messaging.api.MessageRegistry;

import java.io.IOException;

import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.databind.ObjectMapper;

/** This class can be used to publish messages easily. */
@ThreadSafe
public class JsonMessagePublisher extends DefaultMessagePublisher {

  public JsonMessagePublisher(ConnectionParameters connectionParameters) throws IOException {
    super(connectionParameters);
    this.customContentType = "application/json";
  }

  public JsonMessagePublisher(
      ConnectionParameters connectionParameters, MessageRegistry registry, ObjectMapper mapper)
      throws IOException {
    super(connectionParameters, registry, mapper);
    this.customContentType = "application/json";
  }
}
