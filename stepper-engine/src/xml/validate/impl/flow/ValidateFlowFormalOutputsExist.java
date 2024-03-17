package xml.validate.impl.flow;

import flow.definition.api.FlowDefinition;
import io.api.IODefinitionData;
import xml.validate.api.Validator;

import java.util.List;

public class ValidateFlowFormalOutputsExist implements Validator {
    private FlowDefinition flowDefinition;

    public ValidateFlowFormalOutputsExist(FlowDefinition flowDefinition) {
        this.flowDefinition = flowDefinition;
    }

    @Override
    public void validate(List<String> errors) {
        Boolean isFound = false;
        for(IODefinitionData formalOutput : flowDefinition.getFlowFormalOutput()) {
            for (IODefinitionData output : flowDefinition.getOutputs()) {

                // Check if equals by name and by type
                if(formalOutput.equals(output) && formalOutput.getDataDefinition().getType().equals(output.getDataDefinition().getType())) {
                    isFound = true;
                }
            }
            if(!isFound) {
                errors.add("The formal output " + formalOutput.getName() + " does not exist in flow name: " + flowDefinition.getName() + ".");
            }
        }
    }
}
