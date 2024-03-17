package xml.parse.impl;

import flow.definition.api.FlowDefinition;
import flow.definition.api.StepUsageDeclaration;
import flow.definition.impl.FlowDefinitionImpl;
import io.api.DataNecessity;
import io.api.IODefinitionData;
import xml.jaxb.schema.generated.STContinuation;
import xml.jaxb.schema.generated.STContinuationMapping;
import xml.jaxb.schema.generated.STFlow;
import xml.jaxb.schema.generated.STInitialInputValue;
import xml.parse.api.Parser;

import java.util.*;
import java.util.stream.Collectors;

public class FlowParser implements Parser {
    private STFlow stFlow;
    private FlowDefinitionImpl flowDefinition = null;

    public FlowParser(STFlow stflow){
        this.stFlow = stflow;
    }

    @Override
    public List<String> parse(){
        flowDefinition = new FlowDefinitionImpl(stFlow.getName(), stFlow.getSTFlowDescription());
        MappingParser mappingParser = new MappingParser(stFlow);
        List<String> errors = mappingParser.parse();

        if(!errors.isEmpty())
            return errors;

        //  Get Mapping
        Map<StepUsageDeclaration, Map<IODefinitionData, IODefinitionData>> flowMapping = mappingParser.getObject();

        // Add steps to flow
        for(StepUsageDeclaration step : flowMapping.keySet()) {
            flowDefinition.addStepToFlow(step, flowMapping.get(step));
        }

        // Update all outputs (after aliasing)
        List<IODefinitionData> outputs = new ArrayList<>();
        for(Map<IODefinitionData, IODefinitionData> map : flowMapping.values()) {
            outputs.addAll(map.values());
        }

        // Check if the flow has formal outputs
        if(!stFlow.getSTFlowOutput().equals("")) {

            // Add formal outputs
            for (String formalName : stFlow.getSTFlowOutput().split(",")) {
                Optional<IODefinitionData> matchOutput = outputs.stream().filter(IOData -> IOData.getName().equals(formalName)).findFirst();

                // Check if not found
                if (!matchOutput.isPresent()) {
                    errors.add("Error in flow name: " + flowDefinition.getName() + ". Formal output name: " + formalName + " dont found in flow's outputs.");
                } else {
                    flowDefinition.addFormalOutput(matchOutput.get());
                }

                // Check if errors occurred
                if (!errors.isEmpty())
                    return errors;
            }
        }

        // Add mandatory inputs
        List<IODefinitionData> currOutputs = new ArrayList<>();
        for(StepUsageDeclaration step : flowDefinition.getStepsInFlow()){
            List<IODefinitionData> mappedInputs = step.getStepDefinition().getInputs().stream().map(value -> flowMapping.get(step).get(value)).collect(Collectors.toList());
            List<IODefinitionData> mappedOutputs = step.getStepDefinition().getOutputs().stream().map(value -> flowMapping.get(step).get(value)).collect(Collectors.toList());

            // Check if there is automatic mapping
            mappedInputs.removeAll(currOutputs); // Remove all the common data
            for(IODefinitionData input : mappedInputs){
                if(input.getNecessity() == DataNecessity.MANDATORY){
                    flowDefinition.addMandatoryInput(input);
                    currOutputs.add(input);
                }
                else
                    flowDefinition.addOptionalInput(input);
            }
            currOutputs.addAll(mappedOutputs);
        }

        // Add continuation
        if(stFlow.getSTContinuations() != null) {
            if(!stFlow.getSTContinuations().getSTContinuation().isEmpty()) {
                for (STContinuation stContinuation : stFlow.getSTContinuations().getSTContinuation()) {
                    String flowName = stContinuation.getTargetFlow();

                    Map<String, String> flowContinuationMappings = new HashMap<>();
                    for (STContinuationMapping stContinuationMapping : stContinuation.getSTContinuationMapping()) {
                        flowContinuationMappings.put(stContinuationMapping.getSourceData(), stContinuationMapping.getTargetData());
                    }
                    flowDefinition.addToContinuationNames(flowName, flowContinuationMappings);
                }
            }
        }


        // Add initialize values
        if(stFlow.getSTInitialInputValues() != null) {
            if (!stFlow.getSTInitialInputValues().getSTInitialInputValue().isEmpty()) {
                for (STInitialInputValue initialInputValue : stFlow.getSTInitialInputValues().getSTInitialInputValue()) {
                    flowDefinition.addToFromDataNameToStringValue(initialInputValue.getInputName(), initialInputValue.getInitialValue());
                }
            }
        }

        return errors;
    }

    public FlowParser load(STFlow stFlow) {
        this.stFlow = stFlow;
        this.flowDefinition = null;
        return this;
    }


    @Override
    public FlowDefinition getObject() {
        return flowDefinition;
    }


}
