package org.gbif.common.messaging.api.messages;

import java.net.URI;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Message is published when the conversion from of dataset from various formats(DwC or Xml) to avro(ExtendedRecord) is done.
 */
public class ExtendedRecordAvailableMessage implements DatasetBasedMessage{
  public static final String ROUTING_KEY = "crawl.conversion.finished";

  private final UUID datasetUuid;
  private final int attempt;
  private final URI inputFile;
  private final String[] interpretTypes;

  @JsonCreator
  public ExtendedRecordAvailableMessage(
    @JsonProperty("datasetUuid") UUID datasetUuid,
    @JsonProperty("attempt") int attempt,
    @JsonProperty("inputFile") URI inputFile,
    @JsonProperty("interpretTypes") String[] interpretTypes
  ) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    this.inputFile = checkNotNull(inputFile, "inputFile can't be null");
    this.interpretTypes = checkNotNull(interpretTypes, "interpretTypes can't be null");
    this.attempt = attempt;
  }

  /**
   *
   * @return datasetUUID for the converted dataset
   */
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  /**
   *
   * @return attempt for the converted dataset
   */
  public int getAttempt() {
    return attempt;
  }

  /**
   *
   * @return path of final dataset in avro format
   */
  public URI getInputFile() {
    return inputFile;
  }

  /**
   * @return types of interpretation - ALL, LOCATION, BASE or etc.
   */
  public String[] getInterpretTypes() {
    return interpretTypes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ExtendedRecordAvailableMessage that = (ExtendedRecordAvailableMessage) o;
    return attempt == that.attempt
           && Objects.equals(datasetUuid, that.datasetUuid)
           && Objects.equals(inputFile,
                             that.inputFile)
           && Arrays.equals(interpretTypes, that.interpretTypes);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(datasetUuid, attempt, inputFile);
    result = 31 * result + Arrays.hashCode(interpretTypes);
    return result;
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }
}