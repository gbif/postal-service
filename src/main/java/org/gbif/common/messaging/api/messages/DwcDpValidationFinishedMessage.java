package org.gbif.common.messaging.api.messages;

import java.util.UUID;

import org.gbif.dp.analysis.api.DatapackageAnalysisResult;
import org.gbif.dp.service.api.DwcDpValidationFinished;

public class DwcDpValidationFinishedMessage extends DwcDpValidationFinished implements DatasetBasedMessage {

  public static final String ROUTING_KEY = "crawl.dwcdp.validation.finished";

  public DwcDpValidationFinishedMessage(UUID datasetUuid, int attempt, Boolean valid,
    DatapackageAnalysisResult validationReport) {
    super(datasetUuid, attempt, valid, validationReport);
  }

  @Override
  public String getRoutingKey() {
    return ROUTING_KEY;
  }
}
