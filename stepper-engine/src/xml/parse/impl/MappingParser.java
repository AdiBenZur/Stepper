package xml.parse.impl;

import flow.definition.api.StepUsageDeclaration;
import io.api.IODefinitionData;
import io.impl.IODefinitionDataImpl;
import xml.jaxb.schema.generated.*;
import xml.parse.api.Parser;
import xml.validate.api.Validator;
import xml.validate.impl.flow.ValidateCustomMapping;
import xml.validate.impl.flow.ValidateFlowLevelAliasing;

import java.util.*;
import java.util.stream.Collectors;

public class MappingParser implements Parser {
    private final STFlow stFlow;
    private Map<StepUsageDeclaration, Map<IODefinitionData, IODefinitionData>> mapping;

    public MappingParser(STFlow stFlow) {
        this.stFlow = stFlow;
    }

    @Override
    public List<String> parse(){

        List<STStepInFlow> stStepsInFlow = stFlow.getSTStepsInFlow().getSTStepInFlow();
        StepParser stepParser = new StepParser();
        mapping = new LinkedHashMap<>();
        List<String> errors = new ArrayList<>();

        List<STStepInFlow> stFlowSteps = stFlow.getSTStepsInFlow().getSTStepInFlow();
        List<StepUsageDeclaration> flowSteps = new ArrayList<>();

        // Ren over steps and insert steps into flow
        for(int i = 0; i < stStepsInFlow.size(); i ++) {

            // Parse every step and add the errors to the error list (if there is)
            List<String> stepsErrors = stepParser.load(stFlow.getSTStepsInFlow().getSTStepInFlow().get(i)).parse();

            // Check if an error occurred in step parser
            if(!stepsErrors.isEmpty())
                return stepsErrors;

            StepUsageDeclaration currentStep = stepParser.getStepUsageDeclaration();

            // Add to list of steps
            flowSteps.add(currentStep);

            // Get step's mapping
            Map<IODefinitionData, IODefinitionData> currentStepMapping = new HashMap<>();
            List<IODefinitionData> allStepDataIO = new ArrayList<>(); // All the step's data

            // Add inputs and outputs to step's list
            allStepDataIO.addAll(currentStep.getStepDefinition().getInputs());
            allStepDataIO.addAll(currentStep.getStepDefinition().getOutputs());

            // Populate mapping - convert the list into a map
            currentStepMapping.putAll(allStepDataIO.stream()
                    .collect(Collectors.toMap( IOData -> IOData, IOData -> IOData)));

            STFlowLevelAliasing flowLevelAliasing = stFlow.getSTFlowLevelAliasing();

            // Check if there is flow level aliasing in this flow
            if(flowLevelAliasing != null) {

                // Create list of alias according to the current step
                List<STFlowLevelAlias> flowLevelAliases = stFlow.getSTFlowLevelAliasing().getSTFlowLevelAlias()
                        .stream()
                        .filter(stepAlias -> stepAlias.getStep().equals(currentStep.getStepName())) // checks if the alias step name equals to current step's name
                        .collect(Collectors.toList()); // If so - add to list

                // Runs over the io step list
                for(STFlowLevelAlias alias : flowLevelAliases) {
                    allStepDataIO.stream()
                            .filter(IOData -> IOData.getName().equals(alias.getSourceDataName())) // Checks if the current step io data's name is equals to the current 'alias' name
                            .forEach(IOData -> {
                                currentStepMapping.put(IOData, new IODefinitionDataImpl(alias.getAlias(), IOData.getDataDefinition(), IOData.getNecessity(), IOData.getUserString()));
                            });
                }
            }

            mapping.put(currentStep, currentStepMapping);
        }

        // Flow level aliasing validation
        if(stFlow.getSTFlowLevelAliasing() != null) {
            Validator flowLevelAliasingValidator = new ValidateFlowLevelAliasing(stFlow, mapping);
            flowLevelAliasingValidator.validate(errors);
        }

        // Check if errors occurred
        if(!errors.isEmpty())
            return errors;


        // Custom mapping
        STCustomMappings stCustomMappings = stFlow.getSTCustomMappings();

        // Custom mapping validator
        if(stCustomMappings != null) {
            Validator validateCustomMapping = new ValidateCustomMapping(stFlow, flowSteps, mapping);
            validateCustomMapping.validate(errors);
        }

        // Check if errors occurred
        if(!errors.isEmpty())
            return errors;

        // Checks if the current flow has custom mapping
        if(stCustomMappings != null) {
            List<STCustomMapping> flowCustomMapping = new ArrayList<>();
            flowCustomMapping.addAll(stCustomMappings.getSTCustomMapping());

            for(StepUsageDeclaration currentStep : mapping.keySet()) { // Runs over the map keys
                Map<IODefinitionData, IODefinitionData> currentStepMapping = mapping.get(currentStep);
                List<IODefinitionData> IOAlias = new ArrayList<>(currentStepMapping.values());

                // Find the correlated alias and replace it with the custom mapping for each custom mapping that related to the current step
                flowCustomMapping.stream()  // Runs over the custom mapping
                        .filter(mapping -> mapping.getTargetStep().equals(currentStep.getStepName())) // Check if the custom mapping is related to the current step
                        .forEach(mapping -> { IOAlias.stream() // If so, run over the io (after alias)
                                .filter(IOData -> IOData.getName().equals(mapping.getTargetData())) // Check if the data equals to the target data
                                .forEach(IOData ->currentStepMapping.put(currentStepMapping.keySet().stream()
                                        .filter(key -> currentStepMapping.get(key).equals(IOData)).findAny().get()
                                        , new IODefinitionDataImpl(mapping.getSourceData(), IOData.getDataDefinition(), IOData.getNecessity(), IOData.getUserString())));
                        });
            }
        }
        return errors;
    }


    @Override
    public Map<StepUsageDeclaration, Map<IODefinitionData, IODefinitionData>> getObject() {
        return mapping;
    }
}
