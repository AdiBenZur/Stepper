package xml.validate.impl.flow;

import flow.definition.api.FlowDefinition;
import io.api.IODefinitionData;
import xml.validate.api.Validator;

import java.util.List;


public class ValidateThereAreNoMandatoryInputsWithTheSameNameAndDifferentType implements Validator {
    FlowDefinition flowDefinition;

    public ValidateThereAreNoMandatoryInputsWithTheSameNameAndDifferentType(FlowDefinition flowDefinition) {
        this.flowDefinition = flowDefinition;
    }


    @Override
    public void validate(List<String> errors) {

        // Run over all mandatory inputs
       for(IODefinitionData mandatoryInput : flowDefinition.getMandatoryInputs()) {

           // Save the type
           Class<?> mandatoryInputType = mandatoryInput.getDataDefinition().getType();
           String inputName = mandatoryInput.getName();
           Integer counter = 0;

           // Run over all mandatory inputs again
           for(IODefinitionData mandatoryInputInner : flowDefinition.getMandatoryInputs()) {
               if(inputName.equals(mandatoryInputInner.getName()))
                   counter ++;

               if(counter > 1) {
                   Class<?> innerInputClass = mandatoryInputInner.getDataDefinition().getType();
                   if(mandatoryInputType.equals(innerInputClass)) {
                       errors.add("Error in flow " + flowDefinition.getName() + ". There are two mandatory inputs with the same name but not the same type. The name is: "
                       + inputName + ", the different types are: " + mandatoryInputType.getSimpleName() + ", " + mandatoryInputInner.getDataDefinition().getClass().getSimpleName());
                       break;
                   }
               }
           }
       }
    }
}
