package manager.system.operation;

import flow.definition.api.FlowDefinition;
import role.RolesManager;
import stepper.Stepper;
import xml.jaxb.schema.generated.STFlow;
import xml.jaxb.schema.generated.STStepper;
import xml.parse.api.Parser;
import xml.parse.impl.FlowParser;
import xml.validate.api.Validator;
import xml.validate.impl.file.ValidateXmlFile;
import xml.validate.impl.flow.ValidateContinuationFlowNames;
import xml.validate.impl.flow.ValidateContinuationsDataTypes;
import xml.validate.impl.flow.ValidateContinuationsFlowNamesWithSaveData;
import xml.validate.impl.flow.ValidateEveryFlowHasUniqueName;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LoaderManagerWhileSavingPreviousSystemFlows {

    private Stepper stepper;

    public LoaderManagerWhileSavingPreviousSystemFlows(Stepper stepper) {
        this.stepper = stepper;
    }

    public List<String> readDataFromXml(String xmlContent) {
        List<String> errors = new ArrayList<>();
        List<FlowDefinition> newFileFlows = new ArrayList<>();

        // Get stStepper object from Jaxb classes
        STStepper stStepper = getXmlStepperObjFromJaxBClasses(xmlContent);

        // Check every flow has unique name
        Validator validateEveryFlowHasUniqueName = new ValidateEveryFlowHasUniqueName(stStepper.getSTFlows());
        validateEveryFlowHasUniqueName.validate(errors);
        if (!errors.isEmpty()) {
            return errors;
        }

        // Start reading data
        for (STFlow stFlow : stStepper.getSTFlows().getSTFlow()) {

            // Check if the flow already exist
            if(!isFlowNameAlreadyExistInStepper(stFlow.getName())) {

                // parse the flow and add in to stepper if ok
                Parser flowParser = new FlowParser(stFlow);
                errors.addAll(flowParser.parse());

                // Check if there are errors
                if (!errors.isEmpty())
                    return errors;

                FlowDefinition flowDefinition = flowParser.getObject();

                flowDefinition.validateFlowStructure(errors);
                newFileFlows.add(flowDefinition);

                if (!errors.isEmpty())
                    return errors;
            }
        }

        // Set continuation and valid
        List<FlowDefinition> continuations = new ArrayList<>();

        Validator validateContinuationsFlowNamesWithSaveData = new ValidateContinuationsFlowNamesWithSaveData(newFileFlows, continuations, stepper.getAllFlows());
        validateContinuationsFlowNamesWithSaveData.validate(errors);

        if (!errors.isEmpty())
            return errors;

        for (FlowDefinition flow : newFileFlows) {
            Validator validateContinuationCustomMappingTypes = new ValidateContinuationsDataTypes(flow, continuations);
            validateContinuationCustomMappingTypes.validate(errors);

            if (!errors.isEmpty())
                return errors;
        }

        // Update number of threads in pool
        if (stStepper.getSTThreadPool() <= 0) {
            errors.add("The amount of threads in thread pool is non negative.");
            return errors;
        }
        if (stepper.getNumberOfThreads() == -1) { // Not initialized yet -> first valid xml

            stepper.setNumberOfThreads(stStepper.getSTThreadPool());
        }

        // No errors at this point!
        // Add flows to stepper
        for(FlowDefinition flowDefinition : newFileFlows) {
            stepper.addFlowToStepper(flowDefinition);
        }

        return errors; // If the list is empty-the flows are valid.
    }

    public Boolean isFlowNameAlreadyExistInStepper(String name) {
        for(FlowDefinition flowInStepper : stepper.getAllFlows()) {
            if(flowInStepper.getName().equals(name))
                return true;
        }
        return false;
    }

    public STStepper getXmlStepperObjFromJaxBClasses(String xmlContent) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlContent.getBytes());
            JAXBContext jaxbContext = JAXBContext.newInstance(STStepper.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (STStepper) jaxbUnmarshaller.unmarshal(inputStream);
        }
        catch (JAXBException e) {
            throw new RuntimeException(e);
        }

    }

}
