package dto.flow;


import dto.io.IODefinitionDataDTO;
import dto.step.StepUsageDeclarationDTO;
import flow.definition.api.StepUsageDeclaration;
import io.api.IODefinitionData;

import java.util.List;
import java.util.Map;

public class FlowInformationDTO {
    private final String name;
    private final String description;
    private final List<String> formalOutputs;
    private final boolean isReadOnly;
    private final List<StepUsageDeclarationDTO> steps;
    private final List<IODefinitionDataDTO> freeInputs;
    private final List<IODefinitionDataDTO> allOutputs;
    private final Map<String, String> fromOutputToStepProduce; // Names
    private final Map<String, List<String>> fromInputToStepsUse;

    public FlowInformationDTO(String name, String description, List<String> formalOutputs, boolean isReadOnly, List<StepUsageDeclarationDTO> steps
            , List<IODefinitionDataDTO> freeInputs, List<IODefinitionDataDTO> allOutputs, Map<String, String> fromOutputToStepProduce
            , Map<String, List<String>> fromInputToStepsUse) {
        this.name = name;
        this.description = description;
        this.formalOutputs = formalOutputs;
        this.isReadOnly = isReadOnly;
        this.steps = steps;
        this.freeInputs = freeInputs;
        this.allOutputs = allOutputs;
        this.fromOutputToStepProduce = fromOutputToStepProduce;
        this.fromInputToStepsUse = fromInputToStepsUse;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getFormalOutputs() {
        return formalOutputs;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public List<StepUsageDeclarationDTO> getSteps() {
        return steps;
    }

    public List<IODefinitionDataDTO> getFreeInputs() {
        return freeInputs;
    }

    public List<IODefinitionDataDTO> getAllOutputs() {
        return allOutputs;
    }

    public Map<String, String> getFromOutputToStepProduce() {
        return fromOutputToStepProduce;
    }

    public Map<String, List<String>> getFromInputToStepsUse() {
        return fromInputToStepsUse;
    }

    public List<String> getLinkedStepsByInputKey(IODefinitionDataDTO input) {
        return fromInputToStepsUse.get(input.getName());
    }

    public String getStepProduceByKey(IODefinitionDataDTO input) {
        return fromOutputToStepProduce.get(input.getName());
    }

}
