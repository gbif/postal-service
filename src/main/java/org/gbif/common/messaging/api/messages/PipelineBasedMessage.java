package org.gbif.common.messaging.api.messages;

import java.util.Set;

public interface PipelineBasedMessage extends DatasetBasedMessage {

  Integer getAttempt();

  Set<String> getPipelineSteps();

  Long getExecutionId();

}
