package xml.validate.impl.flow;

import flow.definition.api.FlowDefinition;
import io.api.IODefinitionData;
import xml.validate.api.Validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidateUniqueFlowOutputsName implements Validator {
    FlowDefinition flowDefinition;

    public ValidateUniqueFlowOutputsName(FlowDefinition flowDefinition) {
        this.flowDefinition = flowDefinition;
    }

    @Override
    public void validate(List<String> errors) {
        Map<String, Integer> outputNamesCount = new HashMap<>();
        for(IODefinitionData output : flowDefinition.getOutputs()) {
            String name = output.getName();
            outputNamesCount.put(name, outputNamesCount.getOrDefault(name, 0) + 1);
        }

        outputNamesCount.keySet().stream().filter(key -> outputNamesCount.get(key) > 1)
                .forEach(key -> errors.add("The output name: " + key + " belongs to " + outputNamesCount.get(key)
                        + " outputs in flow name: " + flowDefinition.getName() + "."));
    }
}
