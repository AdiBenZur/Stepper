package xml.validate.impl.flow;

import flow.definition.api.StepUsageDeclaration;
import io.api.IODefinitionData;
import xml.jaxb.schema.generated.STFlow;
import xml.jaxb.schema.generated.STFlowLevelAlias;
import xml.validate.api.Validator;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ValidateFlowLevelAliasing implements Validator {
    private final STFlow stFlow;
    private final Map<StepUsageDeclaration, Map<IODefinitionData, IODefinitionData>> flowMapping;

    public ValidateFlowLevelAliasing(STFlow stFlow, Map<StepUsageDeclaration, Map<IODefinitionData, IODefinitionData>> flowMapping) {
        this.stFlow = stFlow;
        this.flowMapping = flowMapping;
    }


    @Override
    public void validate(List<String> errors) {
        // Run over flow level aliasing
        for(STFlowLevelAlias flowLevelAlias : stFlow.getSTFlowLevelAliasing().getSTFlowLevelAlias()) {
            // Check if all the steps are exist in system or exist in flow
            Integer counter = 0;
            for(StepUsageDeclaration stepUsageDeclaration : flowMapping.keySet()) {
                if(stepUsageDeclaration.getStepName().equals(flowLevelAlias.getStep()))
                    counter ++;
            }
            if(counter != 1)
                errors.add("Error is flow name: " + stFlow.getName() + ". In flow level aliasing, the step: " + flowLevelAlias.getStep() + " is not defined in flow");

            // Check if there is a reference to data that not exist in flow
            for(StepUsageDeclaration stepUsageDeclaration : flowMapping.keySet()) {
                if(stepUsageDeclaration.getStepName().equals(flowLevelAlias.getStep())) {
                    Stream<IODefinitionData> dataStream = flowMapping.get(stepUsageDeclaration).keySet().stream().filter(data -> data.getName().equals(flowLevelAlias.getSourceDataName()));
                    if(dataStream.count() != 1)
                        errors.add("Error is flow name: " + stFlow.getName() + ". In flow level aliasing, the data: "
                                + flowLevelAlias.getSourceDataName() + " is not defined in step name: " + stepUsageDeclaration.getStepName());
                }
            }
        }
    }
}
