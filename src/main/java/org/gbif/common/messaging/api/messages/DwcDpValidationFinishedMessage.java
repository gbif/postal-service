package org.gbif.common.messaging.api.messages;

import com.fasterxml.jackson.annotation.JsonCreator;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

import org.gbif.dp.analysis.api.DatapackageAnalysisResult;
import org.gbif.dp.service.api.DwcDpValidationFinished;

public class DwcDpValidationFinishedMessage extends DwcDpValidationFinished implements DatasetBasedMessage {

  public static final String ROUTING_KEY = "crawl.dwcdp.validation.finished";

  @JsonCreator
  public DwcDpValidationFinishedMessage(
    @JsonProperty("datasetUuid") UUID datasetUuid,
    @JsonProperty("attempt") int attempt,
    @JsonProperty("valid") Boolean valid,
    @JsonProperty("validationReport") DatapackageAnalysisResult validationReport) {
    super(datasetUuid, attempt, valid, validationReport);
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }
}
