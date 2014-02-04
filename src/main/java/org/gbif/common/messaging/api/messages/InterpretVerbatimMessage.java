package org.gbif.common.messaging.api.messages;

import org.gbif.common.messaging.api.Message;

import com.google.common.base.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * This message instructs the processors to interpret an Occurrence from a stored VerbatimOccurrence.
 */
public class InterpretVerbatimMessage implements Message {

  private final int occurrenceKey;

  @JsonCreator
  public InterpretVerbatimMessage(@JsonProperty("occurrenceKey") int occurrenceKey) {
    checkArgument(occurrenceKey > 0, "occurrenceKey must be greater than 0");
    this.occurrenceKey = occurrenceKey;
  }

  public int getOccurrenceKey() {
    return occurrenceKey;
  }

  @Override
  public String getRoutingKey() {
    return "occurrence.interpret.occurrence";
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
    final InterpretVerbatimMessage other = (InterpretVerbatimMessage) obj;
    return Objects.equal(this.occurrenceKey, other.occurrenceKey);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("occurrenceKey", occurrenceKey).toString();
  }
}
