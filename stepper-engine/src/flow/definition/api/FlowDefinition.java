package flow.definition.api;

import io.api.IODefinitionData;
import manager.system.operation.FlowInformation;

import java.util.List;
import java.util.Map;

public interface FlowDefinition {

    String getName();
    String getFlowDescription();
    boolean getReadOnly();
    List<StepUsageDeclaration> getStepsInFlow();
    void addStepToFlow(StepUsageDeclaration newStep, Map<IODefinitionData, IODefinitionData> newStepMapping);
    List<IODefinitionData> getFlowFormalOutput();
    List<IODefinitionData> getOutputs();
    Map<StepUsageDeclaration, Map<IODefinitionData, IODefinitionData>> getMappings();
    void addFormalOutput(IODefinitionData output);
    void setFlowReadOnly(boolean ReadOnly);
    void addOutputToFlow(IODefinitionData newOutput);
    List<IODefinitionData> getMandatoryInputs();
    List<IODefinitionData> getOptionalInputs();
    void addMandatoryInput(IODefinitionData newMandatoryInput);
    void addOptionalInput(IODefinitionData newOptionalInput);
    void CheckIfReadOnlyAndSetMember();
    IODefinitionData findValueAccordingToKeyInMappings(StepUsageDeclaration stepUsageDeclaration , IODefinitionData data);
    Map<IODefinitionData, StepUsageDeclaration> getFromOptionalInputToStep();
    Map<IODefinitionData, StepUsageDeclaration> getFromMandatoryInputToStep();
    FlowInformation getFlowInformation();
    StepUsageDeclaration fromMandatoryInputKeyToStep(IODefinitionData data);
    StepUsageDeclaration fromOutputToStepProduce(IODefinitionData data);
    IODefinitionData isOutputConnectToInput(StepUsageDeclaration incomingStep, IODefinitionData output);
    IODefinitionData isInputConnectToOutput(StepUsageDeclaration incomingStep, IODefinitionData input);
    void addToContinuationNames(String flow, Map<String, String> flowMap);
    void addToFromDataNameToStringValue(String dataName, String value);
    Map<String, String> getInitValues();
    Map<String, Map<String, String>> getContinuationMappingNames();
    void addToContinuationCustomMapping(FlowDefinition flow, Map<IODefinitionData, IODefinitionData> data);
    Map<FlowDefinition, Map<IODefinitionData, IODefinitionData>> getFlowContinuationCustomMapping();

    void validateFlowStructure(List<String> errors);

}
