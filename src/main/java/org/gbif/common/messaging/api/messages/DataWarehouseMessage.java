package org.gbif.common.messaging.api.messages;

import org.gbif.api.model.pipelines.PipelinesWorkflow;
import org.gbif.api.model.pipelines.StepType;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

public class DataWarehouseMessage extends PipelinesHdfsViewMessage {

  public DataWarehouseMessage() {}

  @JsonCreator
  public DataWarehouseMessage(
      @JsonProperty("datasetUuid") UUID datasetUuid,
      @JsonProperty("attempt") int attempt,
      @JsonProperty("pipelineSteps") Set<String> pipelineSteps,
      @JsonProperty("runner") String runner,
      @JsonProperty("executionId") Long executionId) {
    super(datasetUuid, attempt, pipelineSteps, runner, executionId);
  }

  @SneakyThrows
  public static void main(String[] args) {
    DataWarehouseMessage message = new DataWarehouseMessage(UUID.fromString("a92f4b3c-ae5c-45af-8dac-4a6a88d35ddd"), 25, Collections.singleton("DATA_WAREHOUSE"), "DISTRIBUTED" ,814400L);
    ObjectMapper mapper = new ObjectMapper();
    System.out.println(mapper.writeValueAsString(message));
    PipelinesWorkflow.Graph<StepType> workflow = PipelinesWorkflow.getOccurrenceWorkflow();
    List<PipelinesWorkflow.Graph<StepType>.Edge> edges = workflow.getNodeEdges(StepType.HDFS_VIEW);
    System.out.println(edges);
  }
}
