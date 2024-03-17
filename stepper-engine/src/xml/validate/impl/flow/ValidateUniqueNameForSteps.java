package xml.validate.impl.flow;

import flow.definition.api.FlowDefinition;
import flow.definition.api.StepUsageDeclaration;
import xml.validate.api.Validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidateUniqueNameForSteps implements Validator {
    private final FlowDefinition flowDefinition;

    public ValidateUniqueNameForSteps(FlowDefinition flowDefinition) {
        this.flowDefinition = flowDefinition;
    }

    @Override
    public void validate(List<String> errors) {
        Map<String, Integer> stepNamesCount = new HashMap<>();
        for(StepUsageDeclaration stepUsageDeclaration : flowDefinition.getStepsInFlow()) {
            String name = stepUsageDeclaration.getStepName();
            stepNamesCount.put(name, stepNamesCount.getOrDefault(name, 0) + 1);
        }

        stepNamesCount.keySet().stream().filter(key -> stepNamesCount.get(key) > 1)
                .forEach(key -> errors.add("Error in flow " + flowDefinition.getName() +". No unique step names: the step name " + key + " shows " + stepNamesCount.get(key)
                        + " times."));
    }
}
