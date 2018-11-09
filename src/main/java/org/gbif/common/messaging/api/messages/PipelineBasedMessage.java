package org.gbif.common.messaging.api.messages;

import java.util.Set;

public interface PipelineBasedMessage extends DatasetBasedMessage {

  int getAttempt();

  Set<String> getPipelineSteps();

}
