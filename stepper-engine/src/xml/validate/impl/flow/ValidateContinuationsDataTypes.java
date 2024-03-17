package xml.validate.impl.flow;

import flow.definition.api.FlowDefinition;
import io.api.DataNecessity;
import io.api.IODefinitionData;
import xml.validate.api.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ValidateContinuationsDataTypes implements Validator {
    private final FlowDefinition flowDefinition;
    private final List<FlowDefinition> continuationFlows;

    public ValidateContinuationsDataTypes(FlowDefinition flowDefinition, List<FlowDefinition> continuationFlows) {
        this.flowDefinition = flowDefinition;
        this.continuationFlows = continuationFlows;
    }

    @Override
    public void validate(List<String> errors) {
        List<IODefinitionData> allFlowData = new ArrayList<>();
        flowDefinition.getMappings().values().forEach(val -> allFlowData.addAll(val.values()));

        // Checks the data types
        for(FlowDefinition continuation : continuationFlows) {
            List<IODefinitionData> continuationsData = new ArrayList<>();
            continuation.getMappings().values().forEach(val -> continuationsData.addAll(val.values()));

            Map<IODefinitionData, IODefinitionData> continuationMap = new HashMap<>();

            // Checks the types
            Map<String, String> customMappings = flowDefinition.getContinuationMappingNames().get(continuation.getName());
            if(customMappings != null) {
                for (String source : customMappings.keySet()) {
                    String target = customMappings.get(source);

                    List<IODefinitionData> sourceData = allFlowData.stream().filter(data -> data.getName().equals(source)).collect(Collectors.toList());
                    List<IODefinitionData> targetData = continuationsData.stream().filter(data -> data.getName().equals(target)).collect(Collectors.toList());

                    // Check if the source exist
                    if (sourceData.isEmpty()) {
                        errors.add("Error in flow " + flowDefinition.getName() + "continuation custom mapping: the source " + source + " does not exist in flow.");
                        return;
                    }

                    // Check if the target exist
                    if (targetData.isEmpty()) {
                        errors.add("Error in flow " + flowDefinition.getName() + "continuation custom mapping: the target " + target + " does not exist in flow " + continuation.getName() + ".");
                        return;
                    }

                    // Check if the target is output
                    if (targetData.stream().anyMatch(data -> data.getNecessity().equals(DataNecessity.NA))) {
                        errors.add("Error in flow " + flowDefinition.getName() + "continuation custom mapping: the target " + target + " is an output in flow " + continuation.getName() + ".");
                        return;
                    }

                    // Check if the types match
                    if (!sourceData.get(0).getDataDefinition().equals(targetData.get(0).getDataDefinition())) {
                        errors.add("Error in flow " + flowDefinition.getName() + "continuation custom mapping: the source " + source + " and the target " + target + " doesnt have the same data definition.");
                        return;
                    }

                    continuationMap.put(sourceData.get(0), targetData.get(0));
                }

                for (IODefinitionData flowData : allFlowData) {
                    List<IODefinitionData> continuationMatch = continuationsData.stream().filter(data -> data.getName().equals(flowData.getName())).collect(Collectors.toList());

                    if (!continuationMatch.isEmpty()) {
                        if (continuationMatch.stream().noneMatch(data -> data.getNecessity().equals(DataNecessity.NA))) {
                            continuationMap.putIfAbsent(flowData, continuationMatch.get(0));
                        }
                    }
                }

                // Add all to the continuation custom mapping
                flowDefinition.addToContinuationCustomMapping(continuation, continuationMap);
            }
        }
    }
}
