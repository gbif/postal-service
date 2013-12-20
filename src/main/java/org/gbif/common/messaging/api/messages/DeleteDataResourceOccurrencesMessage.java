package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Message;

import com.google.common.base.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This message instructs the occurrence deletion service to delete all occurrence records for the given data resource
 * id (the legacy id from the MySQL portal).
 */
public class DeleteDataResourceOccurrencesMessage implements Message {

  private final int dataResourceId;

  @JsonCreator
  public DeleteDataResourceOccurrencesMessage(@JsonProperty("dataResourceId") int dataResourceId) {
    this.dataResourceId = dataResourceId;
  }

  @Override
  public String getRoutingKey() {
    return "occurrence.delete.dataresource";
  }

  public int getDataResourceId() {
    return dataResourceId;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(dataResourceId);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final DeleteDataResourceOccurrencesMessage other = (DeleteDataResourceOccurrencesMessage) obj;
    return Objects.equal(this.dataResourceId, other.dataResourceId);
  }
}
