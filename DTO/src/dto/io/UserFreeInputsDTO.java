package dto.io;

import io.api.DataNecessity;
import io.api.IODefinitionData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserFreeInputsDTO {
    private final String flowName;
    private final List<IODefinitionDataDTO> freeInputsToInsert;
    private final Map<String, String> fromInputToStep;
    private Map<String, String> fromInputToDataInserted;

    public UserFreeInputsDTO(List<IODefinitionDataDTO> freeInputsToInsert, Map<String, String> fromInputToStep, String name) {
        this.freeInputsToInsert = freeInputsToInsert;
        this.fromInputToStep = fromInputToStep;
        this.flowName = name;
        fromInputToDataInserted = new HashMap<>();
    }

    public UserFreeInputsDTO(String flowName, List<IODefinitionDataDTO> freeInputsToInsert, Map<String, String> fromInputToStep, Map<String, String> fromInputToDataInserted) {
        this.flowName = flowName;
        this.freeInputsToInsert = freeInputsToInsert;
        this.fromInputToStep = fromInputToStep;
        this.fromInputToDataInserted = fromInputToDataInserted;
    }

    public void setFromInputToDataInserted(Map<String, String> fromInputToDataInserted) {
        this.fromInputToDataInserted = fromInputToDataInserted;
    }

    public Map<String, String> getFromInputToDataInserted() {
        return fromInputToDataInserted;
    }

    public List<IODefinitionDataDTO> getFreeInputsToInsert() {
        return freeInputsToInsert;
    }

    public void addToMapFromInputToDataInserted(String inputName, String value) {
        fromInputToDataInserted.put(inputName, value);
    }

    public List<IODefinitionDataDTO> getMandatoryFreeInputs() {
        List<IODefinitionDataDTO> mandatoryFreeInputs = new ArrayList<>();
        for(IODefinitionDataDTO input : freeInputsToInsert) {
            if(input.getNecessity().equals("MANDATORY"))
                mandatoryFreeInputs.add(input);
        }
        return mandatoryFreeInputs;
    }

    public List<IODefinitionDataDTO> getOptionalFreeInputs() {
        List<IODefinitionDataDTO> optionalFreeInputs = new ArrayList<>();
        for(IODefinitionDataDTO input : freeInputsToInsert) {
            if(input.getNecessity().equals("OPTIONAL"))
                optionalFreeInputs.add(input);
        }
        return optionalFreeInputs;
    }

    public String findStepNameByInput(IODefinitionDataDTO input) {
        return fromInputToStep.get(input.getName());
    }

    public String getFlowName() {
        return flowName;
    }
}
