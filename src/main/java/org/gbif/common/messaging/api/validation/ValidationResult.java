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
package org.gbif.common.messaging.api.validation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ValidationResult {

  private boolean tripletValid;
  private boolean occurrenceIdValid;
  private Boolean useExtendedRecordId;
  private Long numberOfRecords;
  private Long numberOfEventRecords;

  public ValidationResult() {}

  @JsonCreator
  public ValidationResult(
      @JsonProperty("tripletValid") boolean tripletValid,
      @JsonProperty("occurrenceIdValid") boolean occurrenceIdValid,
      @JsonProperty("useExtendedRecordId") Boolean useExtendedRecordId,
      @JsonProperty("numberOfRecords") Long numberOfRecords,
      @JsonProperty("numberOfEventRecords") Long numberOfEventRecords) {
    this.tripletValid = tripletValid;
    this.occurrenceIdValid = occurrenceIdValid;
    this.useExtendedRecordId = useExtendedRecordId;
    this.numberOfRecords = numberOfRecords;
    this.numberOfEventRecords = numberOfEventRecords;
  }

  public ValidationResult setTripletValid(boolean tripletValid) {
    this.tripletValid = tripletValid;
    return this;
  }

  public ValidationResult setOccurrenceIdValid(boolean occurrenceIdValid) {
    this.occurrenceIdValid = occurrenceIdValid;
    return this;
  }

  public ValidationResult setUseExtendedRecordId(Boolean useExtendedRecordId) {
    this.useExtendedRecordId = useExtendedRecordId;
    return this;
  }

  public ValidationResult setNumberOfRecords(Long numberOfRecords) {
    this.numberOfRecords = numberOfRecords;
    return this;
  }

  public ValidationResult setNumberOfEventRecords(Long numberOfEventRecords) {
    this.numberOfEventRecords = numberOfEventRecords;
    return this;
  }

  public boolean isTripletValid() {
    return tripletValid;
  }

  public boolean isOccurrenceIdValid() {
    return occurrenceIdValid;
  }

  public Boolean isUseExtendedRecordId() {
    return useExtendedRecordId;
  }

  public Long getNumberOfRecords() {
    return numberOfRecords;
  }

  public Long getNumberOfEventRecords() {
    return numberOfEventRecords;
  }
}
