package xml.validate.impl.flow;

import flow.definition.api.FlowDefinition;
import stepper.Stepper;
import xml.validate.api.Validator;

import java.util.List;
import java.util.Map;

public class ValidateContinuationFlowNames implements Validator {

    private final List<FlowDefinition> flows;
    private List<FlowDefinition> continuationFlows;


    public ValidateContinuationFlowNames(List<FlowDefinition> list, List<FlowDefinition> continuations) {
        this.flows = list;
        continuationFlows = continuations;
    }

    @Override
    public void validate(List<String> errors) {

        // Run over flows in stepper
        for(FlowDefinition flowDefinition : flows) {

            // Validate flows names exist
            validFlowsName(flowDefinition, errors);
        }

    }


    public void validFlowsName(FlowDefinition flowDefinition, List<String> errors) {
        Map<String, Map<String, String>> flowContinuation = flowDefinition.getContinuationMappingNames();
        if(!flowContinuation.isEmpty()) {
            for (String flowName : flowContinuation.keySet()) {
                FlowDefinition matchFlowToName = isNameExistInStepper(flowName);
                if(matchFlowToName == null)
                    errors.add("Error in flow " + flowDefinition.getName() + " continuation: flow name " + flowName + " does not exist in the system.");
                else
                    continuationFlows.add(matchFlowToName);
            }
        }
    }

    public FlowDefinition isNameExistInStepper(String name) {
        for(FlowDefinition flowDefinition : flows) {
            if(flowDefinition.getName().equals(name))
                return flowDefinition;
        }
        return null;
    }
}
