package dto.step;

import dto.io.IODefinitionDataDTO;
import step.api.StepDefinition;


import java.util.List;
import java.util.stream.Collectors;

public class StepDefinitionDTO {
    private final String stepName;
    private final boolean isReadOnly;
    private String stepResult;
    private final List<IODefinitionDataDTO> inputs;
    private final List<IODefinitionDataDTO> outputs;


    public StepDefinitionDTO(StepDefinition stepDefinition) {
        this.stepName = stepDefinition.getName();
        this.isReadOnly = stepDefinition.isReadOnly();

        try {
            this.stepResult = stepDefinition.getStepResult();
        }
        catch (Exception e) {
            this.stepResult = null;
        }

        List<IODefinitionDataDTO> inputs = stepDefinition.getInputs()
                .stream()
                .map(IODefinitionDataDTO::new)
                .collect(Collectors.toList());

        this.inputs = inputs;

        List<IODefinitionDataDTO> outputs = stepDefinition.getOutputs()
                .stream()
                .map(IODefinitionDataDTO::new)
                .collect(Collectors.toList());

        this.outputs = outputs;
    }

    public String getStepName() {
        return stepName;
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public String getStepResult() {
        return stepResult;
    }

    public List<IODefinitionDataDTO> getInputs() {
        return inputs;
    }

    public List<IODefinitionDataDTO> getOutputs() {
        return outputs;
    }
}
