package xml.validate.impl.flow;

import flow.definition.api.FlowDefinition;
import io.api.DataNecessity;
import io.api.IODefinitionData;
import xml.validate.api.Validator;

import java.util.List;

public class ValidateNoMandatoryInputThatNotUserFriendly implements Validator {
    private FlowDefinition flowDefinition;

    public ValidateNoMandatoryInputThatNotUserFriendly(FlowDefinition flowDefinition) {
        this.flowDefinition = flowDefinition;
    }

    @Override
    public void validate(List<String> errors) {
        for(IODefinitionData mandatoryInput : flowDefinition.getMandatoryInputs()) {
            if( mandatoryInput.getNecessity().equals(DataNecessity.MANDATORY) && !mandatoryInput.getDataDefinition().isUserFriendly()) {
                errors.add("The mandatory input: " + mandatoryInput.getName() + " is not user friendly.");
            }
        }
    }
}
