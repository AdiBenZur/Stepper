package xml.validate.impl.flow;

import xml.jaxb.schema.generated.STFlow;
import xml.jaxb.schema.generated.STFlows;
import xml.validate.api.Validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ValidateEveryFlowHasUniqueName implements Validator {
    private final STFlows stFlows;

    public ValidateEveryFlowHasUniqueName(STFlows stFlows) {
        this.stFlows = stFlows;
    }


    @Override
    public void validate(List<String> errors) {
        Map<String, Integer> flowNamesCount = new HashMap<>();
        for(STFlow stFlow : stFlows.getSTFlow()) {
            String name = stFlow.getName();
            flowNamesCount.put(name, flowNamesCount.getOrDefault(name, 0) + 1);
        }

        // Runs over the map and checks if every flow has unique name
        flowNamesCount.keySet().stream().filter(key -> flowNamesCount.get(key) > 1)
                .forEach(key -> errors.add("Flow name error: the flow name " + key + " belongs to " + flowNamesCount.get(key) + " different flows."));
    }
}
