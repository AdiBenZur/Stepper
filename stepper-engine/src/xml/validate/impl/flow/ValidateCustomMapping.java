package xml.validate.impl.flow;

import flow.definition.api.StepUsageDeclaration;
import io.api.DataNecessity;
import io.api.IODefinitionData;
import xml.jaxb.schema.generated.STCustomMapping;
import xml.jaxb.schema.generated.STFlow;
import xml.validate.api.Validator;
import java.util.List;
import java.util.Map;


public class ValidateCustomMapping implements Validator {
    private static final int NOT_FOUND = -1;

    private final STFlow stFlow;
    private final List<StepUsageDeclaration> flowSteps;
    private final Map<StepUsageDeclaration, Map<IODefinitionData, IODefinitionData>> flowMapping;

    public ValidateCustomMapping(STFlow stFlow, List<StepUsageDeclaration> flowSteps, Map<StepUsageDeclaration, Map<IODefinitionData, IODefinitionData>> flowMapping) {
        this.stFlow = stFlow;
        this.flowSteps = flowSteps;
        this.flowMapping = flowMapping;
    }

    @Override
    public void validate(List<String> errors) {
        // Run over step custom mapping
        for(STCustomMapping customMapping : stFlow.getSTCustomMappings().getSTCustomMapping()) {
            int sourceStepLocation = NOT_FOUND;
            int targetStepLocation = NOT_FOUND;

            // find location
            for(int i = 0; i < flowSteps.size(); i ++) {
                // Get source step location
                if(flowSteps.get(i).getStepName().equals(customMapping.getSourceStep()))
                    sourceStepLocation = i;

                // Get target step location
                if(flowSteps.get(i).getStepName().equals(customMapping.getTargetStep()))
                    targetStepLocation = i;
            }

            // Check if the source step comes before the target step
            if(sourceStepLocation > targetStepLocation) {
                errors.add("Error in flow name: " + stFlow.getName() + ". In flow custom mapping, the step " + customMapping.getSourceStep() + " comes after step " + customMapping.getTargetStep());
                return;
            }

            // Check if the source step exist in flow steps
            if(sourceStepLocation == NOT_FOUND) {
                errors.add("Error in flow name: " + stFlow.getName() + ". In flow custom mapping, the step: " + customMapping.getSourceStep() + " is not defined in flow");
                return;
            }

            // If the step exist, check if data exists in step
            Boolean isFound = false;
            StepUsageDeclaration stepUsageDeclarationSource = flowSteps.get(sourceStepLocation);
            Map<IODefinitionData, IODefinitionData> sourceStepMapping = flowMapping.get(stepUsageDeclarationSource);
            for(IODefinitionData data : sourceStepMapping.values()) {
                if(data.getNecessity() == DataNecessity.NA && data.getName().equals(customMapping.getSourceData()))
                    isFound = true;
            }
            if(!isFound) {
                errors.add("Error in flow name: " + stFlow.getName() + ". In flow custom mapping, the source data: " + customMapping.getSourceData() +
                        " is not defined in step: " + stepUsageDeclarationSource.getStepName() );
                return;
            }


            // Check if the target step exist in flow steps
            isFound = false;

            if(targetStepLocation == NOT_FOUND) {
                errors.add("Error in flow name: " + stFlow.getName() + ". In flow custom mapping, the step " + customMapping.getTargetStep() + " is not defined in flow");
                return;
            }

            // If the step exist, check if data exists in step
            StepUsageDeclaration stepUsageDeclarationTarget = flowSteps.get(targetStepLocation);
            Map<IODefinitionData, IODefinitionData> targetStepMapping = flowMapping.get(stepUsageDeclarationSource);
            for(IODefinitionData data : targetStepMapping.values()) {
                if(data.getNecessity() == DataNecessity.NA && data.getName().equals(customMapping.getSourceData()))
                    isFound = true;
            }
            if(!isFound) {
                errors.add("Error in flow name: " + stFlow.getName() + ". In flow custom mapping, the target data: " + customMapping.getTargetData() +
                        " is not defined in step: " + stepUsageDeclarationTarget.getStepName() );
                return;
            }
        }
    }

}
