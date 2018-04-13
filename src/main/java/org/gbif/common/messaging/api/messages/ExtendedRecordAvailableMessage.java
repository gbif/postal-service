package org.gbif.common.messaging.api.messages;

import java.net.URI;
import java.util.UUID;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Message is published when the conversion from of dataset from various formats(DwC or Xml) to avro(ExtendedRecord) is done.
 */
public class ExtendedRecordAvailableMessage implements DatasetBasedMessage{
  public static final String ROUTING_KEY = "crawl.conversion.finished";

  private final UUID datasetUuid;
  private final URI hdfsPath;


  @JsonCreator
  public ExtendedRecordAvailableMessage(
    @JsonProperty("datasetUuid") UUID datasetUuid,
    @JsonProperty("hdfsPath") URI hdfsPath
  ) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    this.hdfsPath = checkNotNull(hdfsPath, "hdfsPath can't be null");
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
   * @return path of final dataset in avro format
   */
  public URI getHdfsPath() {
    return hdfsPath;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ExtendedRecordAvailableMessage that = (ExtendedRecordAvailableMessage) o;

    if (!datasetUuid.equals(that.datasetUuid)) return false;
    return hdfsPath.equals(that.hdfsPath);
  }

  @Override
  public int hashCode() {
    int result = datasetUuid.hashCode();
    result = 31 * result + hdfsPath.hashCode();
    return result;
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }
}
