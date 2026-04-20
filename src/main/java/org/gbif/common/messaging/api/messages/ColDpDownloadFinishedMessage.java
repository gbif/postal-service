package org.gbif.common.messaging.api.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.annotation.Nullable;

import java.net.URI;
import java.util.Date;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

import org.gbif.api.vocabulary.EndpointType;
import org.gbif.utils.PreconditionUtils;

public class ColDpDownloadFinishedMessage implements DatasetBasedMessage {
  public static final String ROUTING_KEY = "crawl.coldp.download.finished";

  private final UUID datasetUuid;
  private final URI source;
  private final int attempt;
  private final Date lastModified;
  private final boolean modified;
  private final EndpointType endpointType;
  private final Platform platform;

  @JsonCreator
  public ColDpDownloadFinishedMessage(
    @JsonProperty("datasetUuid") UUID datasetUuid,
    @JsonProperty("source") URI source,
    @JsonProperty("attempt") int attempt,
    @Nullable @JsonProperty("lastModified") Date lastModified,
    @JsonProperty("modified") boolean modified,
    @JsonProperty("endpointType") EndpointType endpointType,
    @JsonProperty("platform") Platform platform) {
    this.datasetUuid = Objects.requireNonNull(datasetUuid, "datasetUuid can't be null");
    this.source = Objects.requireNonNull(source, "source can't be null");
    PreconditionUtils.checkArgument(attempt > 0, "attempt has to be greater than 0");
    this.attempt = attempt;
    this.lastModified = lastModified;
    this.modified = modified;
    this.endpointType = endpointType;
    this.platform = platform != null ? platform : Platform.ALL;
  }

  /** @return dataset uuid */
  @Override
  public UUID getDatasetUuid() {
    return datasetUuid;
  }

  /** @return source the archive has been downloaded from */
  public URI getSource() {
    return source;
  }

  public int getAttempt() {
    return attempt;
  }

  public EndpointType getEndpointType() {
    return endpointType;
  }

  /** @return the date the downloaded archive was last modified or null e.g. for failed downloads */
  @Nullable
  public Date getLastModified() {
    return lastModified;
  }

  /**
   * @return true if the archive has changed since we last downloaded it or never been downloaded
   *     before
   */
  public boolean isModified() {
    return modified;
  }

  /** @return platform that must index the Abcd fragment */
  public Platform getPlatform() {
    return platform;
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ColDpDownloadFinishedMessage that = (ColDpDownloadFinishedMessage) o;
    return attempt == that.attempt
      && modified == that.modified
      && Objects.equals(datasetUuid, that.datasetUuid)
      && Objects.equals(source, that.source)
      && Objects.equals(lastModified, that.lastModified)
      && endpointType == that.endpointType
      && platform == that.platform;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
      datasetUuid, source, attempt, lastModified, modified, endpointType, platform);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", CamtrapDpDownloadFinishedMessage.class.getSimpleName() + "[", "]")
      .add("datasetUuid=" + datasetUuid)
      .add("source=" + source)
      .add("attempt=" + attempt)
      .add("lastModified=" + lastModified)
      .add("modified=" + modified)
      .add("endpointType=" + endpointType)
      .add("platform=" + platform)
      .toString();
  }
}
