package xml.validate.impl.flow;

import exception.data.TypeDontMatchException;
import flow.definition.api.FlowDefinition;
import flow.definition.api.StepUsageDeclaration;
import io.api.IODefinitionData;
import io.impl.UserFreeInputs;
import xml.validate.api.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidateInitializeInputs implements Validator {

    private FlowDefinition flowDefinition;

    public ValidateInitializeInputs(FlowDefinition flowDefinition) {
        this.flowDefinition = flowDefinition;
    }

    @Override
    public void validate(List<String> errors) {
        Map<String, String> initializeValues = flowDefinition.getInitValues();
        UserFreeInputs userFreeInputs = getFlowUserInputs(flowDefinition);
        List<IODefinitionData> freeInputs = userFreeInputs.getFreeInputs();

        // Validate names
        for(String inputName : initializeValues.keySet()) {
            boolean isFound = false;
            for(IODefinitionData data : freeInputs) {
                if(data.getName().equals(inputName)) {
                    isFound = true;
                    break;
                }
            }
            if(!isFound) {
                errors.add("Error in flow " +  flowDefinition.getName() + ". In initialize value- the input " + inputName + " is not a free input.");
            }
        }
        if(errors.isEmpty()) {
            // store data
            try {
                userFreeInputs.insertInitValues();
            } catch (TypeDontMatchException e) {
                errors.add("Error in storing initialize values in flow name " + flowDefinition.getName() + ": " + e.getMessage() );
            }
        }
    }

    public UserFreeInputs getFlowUserInputs(FlowDefinition flowDefinition) {
        List<IODefinitionData> freeInputs = new ArrayList<>();
        freeInputs.addAll(flowDefinition.getMandatoryInputs());
        freeInputs.addAll(flowDefinition.getOptionalInputs());

        Map<IODefinitionData, StepUsageDeclaration> fromInputToStep = new HashMap<>();
        freeInputs.forEach(data -> {
            fromInputToStep.put(data, flowDefinition.fromMandatoryInputKeyToStep(data));
        });
        return new UserFreeInputs(freeInputs, fromInputToStep, flowDefinition.getInitValues());
    }
}
