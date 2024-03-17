package flow.execution.context;

import datadefinition.api.DataDefinition;
import exception.data.NoInputFoundException;
import exception.data.TypeDontMatchException;
import flow.definition.api.StepUsageDeclaration;
import flow.execution.log.StepLog;
import flow.execution.summaryline.StepSummaryLine;
import io.api.IODefinitionData;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FlowExecutionContextImpl implements FlowExecutionContext {

    private StepUsageDeclaration currentStepRunning;
    private Map<String, Object> dataValues; // All step's inputs and outputs
    private final Map<StepUsageDeclaration, Map<IODefinitionData, IODefinitionData>> flowMapping;
    private List<StepLog> flowLogs;
    private List<StepSummaryLine> allStepsSummaryLine;

    public FlowExecutionContextImpl( Map<StepUsageDeclaration, Map<IODefinitionData, IODefinitionData>> flowMapping) {
        this.flowMapping = flowMapping;
        dataValues = new HashMap<>();
        flowLogs = new ArrayList<>();
        allStepsSummaryLine = new ArrayList<>();
        this.currentStepRunning = null;
    }



    @Override
    public <T> T getDataValue(String dataName, Class<T> expectedDataType) throws NoInputFoundException, TypeDontMatchException {
        // dataName is the regular name
        String notOriginalName = null;
        // Get the data definition of the input/ output by its name.
        DataDefinition expected = null;


        Map<IODefinitionData, IODefinitionData> stepMapping = flowMapping.get(currentStepRunning);
        for(IODefinitionData data : stepMapping.keySet()) {
            if(data.getName().equals(dataName)) {
                expected = stepMapping.get(data).getDataDefinition();
                notOriginalName = stepMapping.get(data).getName();
                break;
            }
        }
        if(expected == null)
            throw new NoInputFoundException("The input: " + dataName + "not found");


        // Checking if the expected data type is exactly like the actual class type.
        if(expectedDataType.isAssignableFrom(expected.getType())) {
            Object value = dataValues.get(notOriginalName);
            //return expectedDataType.cast(value);

            T casted = expectedDataType.cast(value);

            if(casted == null)
                throw new NoInputFoundException("The input: " + dataName + " not found");

            return casted;
       }
        else
            throw new TypeDontMatchException("The expected type is different from the actual type. The expected type is: " + expectedDataType.getSimpleName()
            + ", the actual type is :" + expected.getType().getSimpleName());
    }


    @Override
    public void storeDataValue(String dataName, Object value) {
        String alias = null;

        Map<IODefinitionData, IODefinitionData> stepMapping = flowMapping.get(currentStepRunning);
        for(IODefinitionData data : stepMapping.keySet()) {
            if(data.getName().equals(dataName))
                alias = stepMapping.get(data).getName();
        }

        // Insert to context
        dataValues.put(alias, value);
    }

    @Override
    public void storeDataValueFreeInputs(String dataName, Object value) {
        dataValues.put(dataName, value);
    }

    @Override
    public void addLog(StepLog log) {
        String originalLog = log.getStepLog();

        // Add hour
        LocalTime localTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        String formattedTime = localTime.format(formatter);
        log.setStepLog(formattedTime + "- " + originalLog);

        flowLogs.add(log);
    }

    @Override
    public void addStepSummaryLine(StepSummaryLine summaryLine) {

        allStepsSummaryLine.add(summaryLine);
    }

    @Override
    public void updateCurrentRunningStep(StepUsageDeclaration newWorker) {
        this.currentStepRunning = newWorker;
    }

    @Override
    public StepUsageDeclaration getCurrentRunningStep() {

        return currentStepRunning;
    }

    @Override
    public void setCurrentStepRunning(StepUsageDeclaration stepUsageDeclaration) {
        this.currentStepRunning = stepUsageDeclaration;
    }

    @Override
    public Map<String, Object> getContextValues() {
        return dataValues;
    }

    @Override
    public List<StepLog> getFlowLogs() {
        return flowLogs;
    }

    @Override
    public IODefinitionData getDataByName(String name) {
        // Gets alias

        // Run over map
        for(Map<IODefinitionData, IODefinitionData> map : flowMapping.values()) {

            // Run over keys
            for(IODefinitionData value : map.values()) {
                if(value.getName().equals(name))
                    return value;
            }
        }
        return null;
    }

    @Override
    public List<StepSummaryLine> getStepsSummaryLine() {
        return allStepsSummaryLine;
    }


    @Override
    public  Map<IODefinitionData, Object> getAllExecutionInputsAndOutputs() {

        Map<IODefinitionData, Object> dataMap = new HashMap<>();

        for(String name : dataValues.keySet()) {
            IODefinitionData data = getDataByName(name);
            dataMap.put(data, dataValues.get(name));
        }

        return dataMap;
    }

}
