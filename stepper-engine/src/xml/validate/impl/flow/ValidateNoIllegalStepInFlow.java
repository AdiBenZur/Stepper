package xml.validate.impl.flow;

import step.impl.registry.StepRegistry;
import xml.jaxb.schema.generated.STFlow;
import xml.jaxb.schema.generated.STStepInFlow;
import xml.validate.api.Validator;

import java.util.List;

public class ValidateNoIllegalStepInFlow implements Validator {
    private final STFlow stFlow;

    public ValidateNoIllegalStepInFlow(STFlow stFlow) {
        this.stFlow = stFlow;
    }

    @Override
    public void validate(List<String> errors) {
        Boolean isFound;
        for(STStepInFlow stStep : stFlow.getSTStepsInFlow().getSTStepInFlow()) {
            isFound = false;
            for (StepRegistry stepRegistry : StepRegistry.values()) {
                if (stepRegistry.getName().equals(stStep.getName())) {
                    isFound = true;
                }
            }
            if(!isFound) {
                errors.add("Flow name: " + stFlow.getName() + "consists of a step name: " + stStep.getName() + " that is not defined in the system. ");
            }
        }
    }

}
