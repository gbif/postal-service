package org.gbif.common.messaging.api.messages;

import org.gbif.api.model.crawler.DwcaValidationReport;
import org.gbif.api.vocabulary.DatasetType;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * We send this every time the dataset metadata found in a darwin core archive has been
 * deposited in the metadata repository. This includes updating the registered dataset metadata and
 * synchronizing potentially all found dataset constituents, e.g. GSDs in CoL.
 */
public class DwcaMetasyncFinishedMessage implements DatasetBasedMessage {

  public static final String ROUTING_KEY = "crawl.dwca.metasync.finished";

  private final UUID datasetUuid;
  private final DatasetType datasetType;
  private final URI source;
  private final int attempt;
  private final Map<String, UUID> constituents;
  private final DwcaValidationReport validationReport;

  @JsonCreator
  public DwcaMetasyncFinishedMessage(
    @JsonProperty("datasetUuid") UUID datasetUuid,
    @JsonProperty("datasetType") DatasetType datasetType,
    @JsonProperty("source") URI source,
    @JsonProperty("attempt") int attempt,
    @JsonProperty("constituents") Map<String, UUID> constituents,
    @JsonProperty("validationReport") DwcaValidationReport validationReport
  ) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    this.datasetType = checkNotNull(datasetType, "datasetType can't be null");
    this.source = checkNotNull(source, "source can't be null");
    checkArgument(attempt > 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    this.constituents = checkNotNull(constituents, "constituents can't be null");
    this.validationReport = checkNotNull(validationReport, "validationReport can't be null");
  }

  /**
   * @return dataset uuid
   */
  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  /**
   * @return the dataset type as given in the registry
   */
  public DatasetType getDatasetType() {
    return datasetType;
  }

  /**
   * @return source the archive has been downloaded from
   */
  public URI getSource() {
    return source;
  }

  public int getAttempt() {
    return attempt;
  }

  /**
   * Defines a mapping of the dwc:datasetID used in the archive data and our UUID datasetUuid
   * for all constituent (aka sub-) datasets found in this archive. Its a convenience map needed for indexing but
   * could also be build by querying the registry constituent datasets and their tags.
   *
   * @return map of dwc:datasetID to datasetUuid
   */
  public Map<String, UUID> getConstituents() {
    return constituents;
  }

  /**
   * @return the validationReport for this archive
   */
  public DwcaValidationReport getValidationReport() {
    return validationReport;
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(datasetUuid, datasetType, source, attempt, constituents, validationReport);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final DwcaMetasyncFinishedMessage other = (DwcaMetasyncFinishedMessage) obj;
    return Objects.equal(this.datasetUuid, other.datasetUuid)
           && Objects.equal(this.datasetType, other.datasetType)
           && Objects.equal(this.source, other.source)
           && Objects.equal(this.attempt, other.attempt)
           && Objects.equal(this.constituents, other.constituents)
           && Objects.equal(this.validationReport, other.validationReport);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
      .add("datasetUuid", datasetUuid)
      .add("datasetType", datasetType)
      .add("source", source)
      .add("attempt", attempt)
      .add("constituents", constituents)
      .add("validationReport", validationReport)
      .toString();
  }
}