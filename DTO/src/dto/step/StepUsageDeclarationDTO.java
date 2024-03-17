package dto.step;

import dto.io.IOInStepMappingDTO;
import flow.definition.api.StepUsageDeclaration;
import io.api.DataNecessity;
import io.api.IODefinitionData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StepUsageDeclarationDTO {
    private final String finalName;
    private final StepDefinitionDTO stepDefinitionDTO;
    private final boolean skipIfFail;
    private final List<IOInStepMappingDTO> inputs; // With flow level aliasing
    private final List<IOInStepMappingDTO> outputs; // With flow level aliasing


    public StepUsageDeclarationDTO(StepUsageDeclaration stepUsageDeclaration, List<IOInStepMappingDTO> inputs, List<IOInStepMappingDTO> outputs) {
        this.finalName = stepUsageDeclaration.getStepName();
        this.stepDefinitionDTO = new StepDefinitionDTO(stepUsageDeclaration.getStepDefinition());
        this.skipIfFail = stepUsageDeclaration.skipIfFail();
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public String getFinalName() {
        return finalName;
    }

    public StepDefinitionDTO getStepDefinitionDTO() {
        return stepDefinitionDTO;
    }

    public boolean isSkipIfFail() {
        return skipIfFail;
    }

    public List<IOInStepMappingDTO> getInputs() {
        return inputs;
    }

    public List<IOInStepMappingDTO> getOutputs() {
        return outputs;
    }




}
