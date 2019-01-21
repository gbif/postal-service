package org.gbif.common.messaging.api.messages;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.gbif.api.model.crawler.DwcaValidationReport;
import org.gbif.api.vocabulary.DatasetType;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * We send this every time a darwin core archive has been validated after being downloaded and its metadata has been
 * synchronized.
 */
public class PipelinesDwcaMessage implements PipelineBasedMessage {

  public static final String ROUTING_KEY = DwcaValidationFinishedMessage.ROUTING_KEY;

  private UUID datasetUuid;
  private DatasetType datasetType;
  private URI source;
  private int attempt;
  private DwcaValidationReport validationReport;
  private Set<String> pipelineSteps;

  public PipelinesDwcaMessage() {
  }

  @JsonCreator
  public PipelinesDwcaMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("datasetType") DatasetType datasetType,
      @JsonProperty("source") URI source,
      @JsonProperty("attempt") int attempt,
      @JsonProperty("validationReport") DwcaValidationReport validationReport,
      @JsonProperty("pipelineSteps") Set<String> pipelineSteps) {
    this.datasetUuid = checkNotNull(datasetUuid, "datasetUuid can't be null");
    this.datasetType = checkNotNull(datasetType, "datasetType can't be null");
    this.source = checkNotNull(source, "source can't be null");
    checkArgument(attempt > 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    this.validationReport = checkNotNull(validationReport, "validationReport can't be null");
    this.pipelineSteps = pipelineSteps == null ? Collections.emptySet() : pipelineSteps;
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

  @Override
  public int getAttempt() {
    return attempt;
  }

  @Override
  public Set<String> getPipelineSteps() {
    return pipelineSteps;
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
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PipelinesDwcaMessage that = (PipelinesDwcaMessage) o;
    return attempt == that.attempt && Objects.equals(datasetUuid, that.datasetUuid) && datasetType == that.datasetType
        && Objects.equals(source, that.source) && Objects.equals(validationReport, that.validationReport)
        && Objects.equals(pipelineSteps, that.pipelineSteps);
  }

  public PipelinesDwcaMessage setDatasetUuid(UUID datasetUuid) {
    this.datasetUuid = datasetUuid;
    return this;
  }

  public PipelinesDwcaMessage setDatasetType(DatasetType datasetType) {
    this.datasetType = datasetType;
    return this;
  }

  public PipelinesDwcaMessage setSource(URI source) {
    this.source = source;
    return this;
  }

  public PipelinesDwcaMessage setAttempt(int attempt) {
    this.attempt = attempt;
    return this;
  }

  public PipelinesDwcaMessage setValidationReport(DwcaValidationReport validationReport) {
    this.validationReport = validationReport;
    return this;
  }

  public PipelinesDwcaMessage setPipelineSteps(Set<String> pipelineSteps) {
    this.pipelineSteps = pipelineSteps;
    return this;
  }

  @Override
  public int hashCode() {
    return Objects.hash(datasetUuid, datasetType, source, attempt, validationReport, pipelineSteps);
  }

  @Override
  public String toString() {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.writeValueAsString(this);
    } catch (IOException e) {
      // NOP
    }
    return "";
  }
}
