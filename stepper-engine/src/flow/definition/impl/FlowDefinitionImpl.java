package flow.definition.impl;

import flow.definition.api.FlowDefinition;
import flow.definition.api.StepUsageDeclaration;
import io.api.DataNecessity;
import io.api.IODefinitionData;
import manager.system.operation.FlowInformation;
import xml.validate.api.Validator;
import xml.validate.impl.flow.*;

import java.util.*;
import java.util.stream.Collectors;

public class FlowDefinitionImpl implements FlowDefinition {

   private final String name;
   private final String description;
   private boolean isReadOnly;
   private final List<StepUsageDeclaration> steps;
   private final List<IODefinitionData> allFlowOutputs; // After alias
   private final List<IODefinitionData> formalOutputs; // After alias
   private final List<IODefinitionData> mandatoryInputs; // Free inputs.
   private final List<IODefinitionData> optionalsInputs;  // After alias
   private final Map<StepUsageDeclaration, Map<IODefinitionData, IODefinitionData>> mappings; // (Step, (data pre alias, data post alias))
   private final Map<IODefinitionData, StepUsageDeclaration> fromOutputToStep; // (IOData, step). Which step produce the output.
                                                                               // Every output has a unique name.
   private final Map<IODefinitionData, List<StepUsageDeclaration>> fromInputToSteps; // (IOData, step). Which step use the input.
   private final Map<IODefinitionData, StepUsageDeclaration> fromMandatoryInputToStep;
   private final Map<IODefinitionData, StepUsageDeclaration> fromOptionalInputToStep;
   private Map<String, Map<String, String >> continuationMappingNames;
   private Map<String, String> initializeValues;
   private Map<FlowDefinition, Map<IODefinitionData, IODefinitionData>> continuationCustomMapping;


    public FlowDefinitionImpl(String name, String description) {
       this.name = name;
       this.description = description;
       allFlowOutputs = new ArrayList<>();
       steps = new ArrayList<>();
       this.isReadOnly = false;
       formalOutputs = new ArrayList<>();
       mandatoryInputs = new ArrayList<>();
       optionalsInputs = new ArrayList<>();
       mappings = new LinkedHashMap<>();
       fromOutputToStep = new HashMap<>();
       fromInputToSteps = new HashMap<>();
       fromMandatoryInputToStep = new HashMap<>();
       fromOptionalInputToStep = new HashMap<>();
        continuationMappingNames = new HashMap<>();
        initializeValues = new HashMap<>();
        continuationCustomMapping = new HashMap<>();
   }


    @Override
    public String getName() { return name; }

    @Override
    public String getFlowDescription() { return description; }

    @Override
    public boolean getReadOnly() { return isReadOnly; }

    @Override
    public List<StepUsageDeclaration> getStepsInFlow() { return steps; }

    @Override
    public List<IODefinitionData> getOutputs() { return allFlowOutputs; }

    @Override
    public Map<StepUsageDeclaration, Map<IODefinitionData, IODefinitionData>> getMappings() { return mappings; }

    @Override
    public void addFormalOutput(IODefinitionData output) { formalOutputs.add(output); }

    @Override
    public void setFlowReadOnly(boolean ReadOnly) { isReadOnly = ReadOnly; }

    @Override
    public void addOutputToFlow(IODefinitionData newOutput) {
        allFlowOutputs.add(newOutput);
    }

    @Override
    public List<IODefinitionData> getMandatoryInputs() { return mandatoryInputs; }

    @Override
    public List<IODefinitionData> getOptionalInputs() { return optionalsInputs; }

    @Override
    public void addMandatoryInput(IODefinitionData newMandatoryInput) { mandatoryInputs.add(newMandatoryInput); }

    @Override
    public void addOptionalInput(IODefinitionData newOptionalInput) { optionalsInputs.add(newOptionalInput); }

    @Override
    public void CheckIfReadOnlyAndSetMember() {
        Boolean flag = true;
        for (StepUsageDeclaration step : steps) {
            if (!step.getStepDefinition().isReadOnly()) {
                flag = false;
            }
        }
        isReadOnly = flag;
    }

    @Override
    public IODefinitionData findValueAccordingToKeyInMappings(StepUsageDeclaration stepUsageDeclaration , IODefinitionData data) {
        Optional<Map<IODefinitionData, IODefinitionData>> stepMapping = Optional.ofNullable(mappings.get(stepUsageDeclaration));
        return stepMapping.map(ioDefinitionDataIODefinitionDataMap -> ioDefinitionDataIODefinitionDataMap.get(data)).orElse(null);
    }

    @Override
    public Map<IODefinitionData, StepUsageDeclaration> getFromOptionalInputToStep() { return fromOptionalInputToStep; }

    @Override
    public Map<IODefinitionData, StepUsageDeclaration> getFromMandatoryInputToStep() { return fromMandatoryInputToStep; }

    @Override
    public FlowInformation getFlowInformation() {
        FlowInformation flowInformation = new FlowInformation(this.name, this.getFlowDescription(), this.formalOutputs, this.isReadOnly,
                this.steps, this.mandatoryInputs, this.allFlowOutputs, this.fromInputToSteps, this.fromOutputToStep );

        return flowInformation;
    }

    @Override
    public StepUsageDeclaration fromMandatoryInputKeyToStep(IODefinitionData data) {
        StepUsageDeclaration stepUsageDeclaration = fromMandatoryInputToStep.get(data);

        // If not found
        if(stepUsageDeclaration == null) {
            stepUsageDeclaration = fromInputToSteps.get(data).get(0);
        }
        return stepUsageDeclaration;
    }

    @Override
    public StepUsageDeclaration fromOutputToStepProduce(IODefinitionData data) {
        return fromOutputToStep.get(data);
    }

    @Override
    public IODefinitionData isOutputConnectToInput(StepUsageDeclaration incomingStep, IODefinitionData output) {
        IODefinitionData result = null;

        for(StepUsageDeclaration step : mappings.keySet()) {

            // Search only in other steps mappings
            if(!step.getStepName().equals(incomingStep.getStepName())) {

                // Get step mapping
                Map<IODefinitionData, IODefinitionData> stepMapping = mappings.get(step);

                // Run over step mapping
                for(IODefinitionData data : stepMapping.values()) {

                    // Check if an input and if that the data match the output
                    if(data.getNecessity() != DataNecessity.NA && data.getName().equals(output.getName()) && data.getDataDefinition().equals(output.getDataDefinition())) {
                        result = data;
                        break;
                    }
                }
            }

            // If found
            if(result != null)
                break;
        }
        return result;
    }

    @Override
    public IODefinitionData isInputConnectToOutput(StepUsageDeclaration incomingStep, IODefinitionData input) {
        IODefinitionData result = null;

        for(StepUsageDeclaration step : mappings.keySet()) {

            // Search only in other steps mappings
            if(!step.getStepName().equals(incomingStep.getStepName())) {

                // Get step mapping
                Map<IODefinitionData, IODefinitionData> stepMapping = mappings.get(step);

                // Run over step mapping
                for(IODefinitionData data : stepMapping.values()) {

                    // Check if an output and if that the data match the input
                    if(data.getNecessity() == DataNecessity.NA && data.getName().equals(input.getName()) && data.getDataDefinition().equals(input.getDataDefinition())) {
                        result = data;
                        break;
                    }
                }
            }

            // If found
            if(result != null)
                break;
        }
        return result;
    }

    @Override
    public void addToContinuationNames(String flow, Map<String, String> flowMap) {
        continuationMappingNames.put(flow, flowMap);
    }

    @Override
    public void addToFromDataNameToStringValue(String dataName, String value) {
        initializeValues.put(dataName, value);
    }

    @Override
    public Map<String, String> getInitValues() {
        return initializeValues;
    }

    @Override
    public Map<String, Map<String, String>> getContinuationMappingNames() {
        return continuationMappingNames;
    }

    @Override
    public void addToContinuationCustomMapping(FlowDefinition flow, Map<IODefinitionData, IODefinitionData> data) {
        continuationCustomMapping.put(flow, data);
    }

    @Override
    public Map<FlowDefinition, Map<IODefinitionData, IODefinitionData>> getFlowContinuationCustomMapping() {
        return continuationCustomMapping;
    }

    @Override
    public void addStepToFlow(StepUsageDeclaration newStep, Map<IODefinitionData, IODefinitionData> newStepMapping) {
       steps.add(newStep);
       mappings.put(newStep, newStepMapping);

       // Add step's outputs to flow's all outputs
        allFlowOutputs.addAll(newStep.getStepDefinition().getOutputs().stream().map(newStepMapping::get).collect(Collectors.toList()));

        // Add to 'fromOutputToStep'
        newStepMapping.values().stream().filter(IOData -> IOData.getNecessity().equals(DataNecessity.NA)) // If the data is an output
                .forEach(output -> fromOutputToStep.put(output, newStep));

        // Add to 'fromInputToSteps'
        newStepMapping.values().stream().filter(IOData -> IOData.getNecessity().equals(DataNecessity.MANDATORY)) // If the data ia a mandatory input
                .forEach(input -> {
                    if(!fromInputToSteps.containsKey(input))
                        fromInputToSteps.put(input, new ArrayList<>());
                    if(!fromInputToSteps.get(input).contains(newStep))
                        fromInputToSteps.get(input).add(newStep);

                    // Add mandatory input and his correlate step to 'fromMandatoryInputToStep'
                    if(!fromMandatoryInputToStep.containsKey(input))
                        fromMandatoryInputToStep.put(input, newStep);

                });

        newStepMapping.values().stream().filter(IOData -> IOData.getNecessity().equals(DataNecessity.OPTIONAL)) // If the data ia a optional input
                .forEach(input -> {
                    if(!fromInputToSteps.containsKey(input))
                        fromInputToSteps.put(input, new ArrayList<>());
                    if(!fromInputToSteps.get(input).contains(newStep))
                        fromInputToSteps.get(input).add(newStep);

                    // Add optional input and his correlate step to 'fromOptionalInputToStep'
                    if(!fromOptionalInputToStep.containsKey(input))
                        fromOptionalInputToStep.put(input, newStep);
                });
    }

    @Override
    public List<IODefinitionData> getFlowFormalOutput() { return formalOutputs; }


    @Override
    public void validateFlowStructure(List<String> errors) {

        Validator validateUniqueFlowFormalOutputNames = new ValidateUniqueFlowOutputsName(this);
        validateUniqueFlowFormalOutputNames.validate(errors);

        Validator validNoMandatoryInputsThatNotUserFriendly = new ValidateNoMandatoryInputThatNotUserFriendly(this);
        validNoMandatoryInputsThatNotUserFriendly.validate(errors);

        Validator validateFlowFormalOutputsExist = new ValidateFlowFormalOutputsExist(this);
        validateFlowFormalOutputsExist.validate(errors);

        Validator validateNoMandatoryInputWithSameNameAndNotSameType = new ValidateThereAreNoMandatoryInputsWithTheSameNameAndDifferentType(this);
        validateNoMandatoryInputWithSameNameAndNotSameType.validate(errors);

        Validator validateEveryStepHasUniqueName = new ValidateUniqueNameForSteps(this);
        validateEveryStepHasUniqueName.validate(errors);

        Validator validateInitializeValues = new ValidateInitializeInputs(this);
        validateInitializeValues.validate(errors);

        CheckIfReadOnlyAndSetMember();
    }




}
