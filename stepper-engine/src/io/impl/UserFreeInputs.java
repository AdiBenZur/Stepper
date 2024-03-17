package io.impl;

import exception.data.NotUserFriendlyException;
import exception.data.TypeDontMatchException;
import flow.definition.api.StepUsageDeclaration;
import io.api.DataNecessity;
import io.api.IODefinitionData;

import java.util.*;

public class UserFreeInputs {
    private List<IODefinitionData> freeInputs;
    private List<IODefinitionData> initializeValues;
    private Map<IODefinitionData, StepUsageDeclaration> fromInputToStep;
    private Map<IODefinitionData, Object> fromInputToObject;
    private final Map<String, String> initValues;

    public UserFreeInputs(List<IODefinitionData> freeInputs, Map<IODefinitionData, StepUsageDeclaration> fromInputToStep, Map<String, String> map) {
        this.freeInputs = freeInputs;
        this.fromInputToStep = fromInputToStep;
        fromInputToObject = new HashMap<>();
        initValues = map;
        initializeValues = new ArrayList<>();
    }

    public void insertInitValues() throws TypeDontMatchException {
        // Fill data
        for(String data : initValues.keySet()) {
            for(IODefinitionData input : freeInputs) {
                if(data.equals(input.getName())) {
                    // Store value
                    scanInput(input, initValues.get(data));
                    initializeValues.add(input);
                }
            }
        }
    }

    public boolean isInputHasInitializeValue(IODefinitionData input) {
        if(initValues.containsKey(input.getName()))
            return true;
        return false;
    }

    public String fromInputToStepName(IODefinitionData input) {
        StepUsageDeclaration stepUsageDeclaration = fromInputToStep.get(input);
        return stepUsageDeclaration.getStepName();
    }

    public List<IODefinitionData> getInitializeValues() {
        return initializeValues;
    }

    public List<IODefinitionData> getFreeInputs() {
        return freeInputs;
    }

    public Map<IODefinitionData, StepUsageDeclaration> getFromInputToStep() {
        return fromInputToStep;
    }

    public Map<IODefinitionData, Object> getFromInputToObject() {
        return fromInputToObject;
    }

    public Boolean isAllMandatoryInsert() {
        for(IODefinitionData data : freeInputs) {
            if(data.getNecessity() == DataNecessity.MANDATORY)
                if(!isDataInserted(data))
                    return false;
        }
        return true;
    }

    public Boolean isDataInserted(IODefinitionData input) {
        if(fromInputToObject.containsKey(input))
            if(fromInputToObject.get(input) != null)
                return true;
        return false;
    }

    // Add input to fromInputToObject
    public void scanInput(IODefinitionData data, String value) throws TypeDontMatchException {
        try {
            fromInputToObject.put(data, data.getDataDefinition().scanInput(value));
        }
        catch (NotUserFriendlyException e) {

        }
    }

    public StepUsageDeclaration findStepByInputKey(IODefinitionData key) {
        return fromInputToStep.get(key);
    }

    public List<IODefinitionData> getMandatoryFreeInputs() {
        List<IODefinitionData> mandatoryFreeInputs = new ArrayList<>();
        for(IODefinitionData input : freeInputs) {
            if(input.getNecessity() == DataNecessity.MANDATORY)
                mandatoryFreeInputs.add(input);
        }
        return mandatoryFreeInputs;
    }

    public List<IODefinitionData> getOptionalFreeInputs() {
        List<IODefinitionData> optionalFreeInputs = new ArrayList<>();
        for(IODefinitionData input : freeInputs) {
            if(input.getNecessity() == DataNecessity.OPTIONAL)
                optionalFreeInputs.add(input);
        }
        return optionalFreeInputs;
    }


}
