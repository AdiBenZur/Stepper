package flow.execution;

import flow.definition.api.FlowDefinition;
import flow.definition.api.StepUsageDeclaration;
import flow.execution.context.FlowExecutionContext;
import flow.execution.context.FlowExecutionContextImpl;
import io.api.DataNecessity;
import io.api.IODefinitionData;
import io.impl.UserFreeInputs;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import step.api.StepDefinition;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class FlowExecution {


    private final UUID uniqueId;
    private final FlowDefinition flowDefinition;
    private FlowExecutionContext context;
    private List<String> flowLogs;
    private String flowSummaryLine;
    private FlowExecutionResult flowExecutionResult;
    private String startTimeOfExecution; // Time of start
    private Duration totalTime; // Flow's duration
    private Map<StepDefinition, Map<StepUsageDeclaration, Duration>> statistics;
    private Map<StepUsageDeclaration, LocalTime> fromStepToStartTime;
    private Map<StepUsageDeclaration, LocalTime> fromStepToEndTime;
    private UserFreeInputs flowFreeInputs;
    private Map<IODefinitionData, Object> allOutputProduceDuringFlow;
    private List<StepUsageDeclaration> stepsInFlow;
    private Map<StepUsageDeclaration, Map<IODefinitionData, IODefinitionData>> flowMapping;
    private Map<StepUsageDeclaration, Duration> fromStepToDuration;
    private SimpleIntegerProperty stepFinishedProperty;
    private SimpleBooleanProperty isExecutionEnded;
    public boolean processing = true;

    public FlowExecution(FlowDefinition flowDefinition, UserFreeInputs userFreeInputs) {

        // flow definition delete execution fill
        for(StepUsageDeclaration stepInFlow : flowDefinition.getStepsInFlow()) {
            stepInFlow.getStepDefinition().initializeResult();
        }
        stepFinishedProperty = new SimpleIntegerProperty(0);
        isExecutionEnded = new SimpleBooleanProperty(false);
        this.uniqueId = UUID.randomUUID();
        this.flowDefinition = flowDefinition;
        allOutputProduceDuringFlow = new HashMap<>();
        flowLogs = new ArrayList<>();
        stepsInFlow = flowDefinition.getStepsInFlow();
        statistics = new HashMap<>();
        fromStepToStartTime = new HashMap<>();
        fromStepToEndTime = new HashMap<>();
        this.flowFreeInputs = userFreeInputs;
        context = new FlowExecutionContextImpl(flowDefinition.getMappings());
        this.flowMapping = flowDefinition.getMappings();
        fromStepToDuration = new HashMap<>();

        // Add free inputs to context
        for(IODefinitionData data : flowFreeInputs.getFromInputToObject().keySet()) {
            // Add to context only if user insert value
            if(flowFreeInputs.getFromInputToObject().get(data) != null)
                context.storeDataValueFreeInputs(data.getName(), flowFreeInputs.getFromInputToObject().get(data));
        }
    }

    public void setProcessing(boolean processing) {
        this.processing = processing;
    }

    public boolean isProcessing() {
        return processing;
    }

    public void setStartTimeOfExecution(String startTimeString) {
        this.startTimeOfExecution = startTimeString;
    }

    public FlowExecutionContext getContext() {
        return context;
    }


    public void setIsExecutionEnded(boolean isExecutionEnded) {
        this.isExecutionEnded.set(isExecutionEnded);
    }

    public SimpleBooleanProperty getIsExecutionEndedProperty() {
        return isExecutionEnded;
    }

    public SimpleIntegerProperty getStepFinishedProperty() {
        return stepFinishedProperty;
    }

    public void setStepFinished(int value) {
        stepFinishedProperty.setValue(value);
    }

    public SimpleIntegerProperty stepFinishedPropertyProperty() {
        return stepFinishedProperty;
    }

    public String getUniqueId() {
        return uniqueId.toString(); }

    public FlowDefinition getFlowDefinition() {
        return flowDefinition; }

    public String getFlowName() {
        return flowDefinition.getName();
    }

    public void setTotalTime(Duration totalTime) {

        this.totalTime = totalTime;
    }

    public void setCurrentStepRunner(StepUsageDeclaration newWorker) {
        context.updateCurrentRunningStep(newWorker);
    }

    public String getFlowExecutionStatus() {
        String result = null;

        switch (flowExecutionResult) {
            case SUCCESS:
                result = "Success";
                break;

            case FAILURE:
                result = "Failure";
                break;

            case WARNING:
                result = "Warning";
                break;

            default:
                result = null;
        }
        return result;
    }

    public String  getStartTimeOfExecution() {
        return startTimeOfExecution;
    }

    public long getTotalTime() { return totalTime.toMillis(); }

    public FlowExecutionResult getFlowExecutionResult() {
        return flowExecutionResult;
    }

    public Map<StepDefinition, Map<StepUsageDeclaration, Duration>> getStatistics() {
        return statistics;
    }

    public UserFreeInputs getFlowFreeInputs() {
        return flowFreeInputs;
    }

    public Map<IODefinitionData, Object> getAllOutputProduceDuringFlow() {
        return allOutputProduceDuringFlow;
    }

    public List<StepUsageDeclaration> getStepsInFlow() {
        return stepsInFlow;
    }

    public void addStatistics(StepUsageDeclaration stepUsageDeclaration, Duration duration) {
        StepDefinition stepDefinition = stepUsageDeclaration.getStepDefinition();

        // Check if the key exists
        if(!statistics.containsKey(stepDefinition)) {
            Map<StepUsageDeclaration, Duration> innerMap = new HashMap<>();
            innerMap.put(stepUsageDeclaration, duration);
            statistics.put(stepDefinition, innerMap);
        }
        else {
            Map<StepUsageDeclaration, Duration> innerMap = statistics.get(stepDefinition);
            innerMap.put(stepUsageDeclaration, duration);
        }
    }

    public void addToFromStepToDuration(StepUsageDeclaration stepUsageDeclaration, Duration duration) {
        fromStepToDuration.put(stepUsageDeclaration, duration);
    }

    public Map<StepUsageDeclaration, Duration> getFromStepToDuration() {
        return fromStepToDuration;
    }

    public String getFlowSummaryLine() {
        return flowSummaryLine;
    }

    public void setFlowSummaryLine(String flowSummaryLine) {
        this.flowSummaryLine = flowSummaryLine;
    }



    public void addFlowLog(String log) {
        flowLogs.add(log);
    }

    public List<String> getFlowLogs() {
        return flowLogs;
    }

    public void setFlowExecutionResult(FlowExecutionResult flowExecutionResult) {
        this.flowExecutionResult = flowExecutionResult;
    }

    public void setAllOutputsData(FlowExecutionContext context) {
        // Runs over keys

        for(String name : context.getContextValues().keySet()) {
            IODefinitionData data = context.getDataByName(name);

            if(data.getNecessity() == DataNecessity.NA) {
                allOutputProduceDuringFlow.put(data, context.getContextValues().get(name));
            }
        }
    }

    public void addStepStartAndEndTime(StepUsageDeclaration step, LocalTime start, LocalTime end) {
        fromStepToStartTime.put(step, start);
        fromStepToEndTime.put(step, end);
    }

    public String  getFromStepToStart(StepUsageDeclaration stepUsageDeclaration) {
        LocalTime result = fromStepToStartTime.get(stepUsageDeclaration);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");
        String formattedTime = result.format(formatter);

        return formattedTime;
    }

    public String  getFromStepToEnd(StepUsageDeclaration stepUsageDeclaration) {
        LocalTime result = fromStepToEndTime.get(stepUsageDeclaration);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");
        String formattedTime = result.format(formatter);

        return formattedTime;
    }

    public Map<StepUsageDeclaration, Map<IODefinitionData, Object>> getFromStepToDataMap() {
        Map<IODefinitionData, Object> dataMap = context.getAllExecutionInputsAndOutputs();

        Map<StepUsageDeclaration, Map<IODefinitionData, Object>> fromStepToDataProduce = new HashMap<>();
        for(StepUsageDeclaration step : flowMapping.keySet()) {
            Map<IODefinitionData, Object> currentStepMap = new HashMap<>(); // Step map result

            Map<IODefinitionData, IODefinitionData> stepMapping = flowMapping.get(step);
            for(IODefinitionData data : stepMapping.values()) {
                for(IODefinitionData dataOnDataMap : dataMap.keySet()) {
                    if(data.equals(dataOnDataMap)) {
                        currentStepMap.put(dataOnDataMap, dataMap.get(dataOnDataMap));
                        break;
                    }
                }
            }
            if(!currentStepMap.isEmpty())
                fromStepToDataProduce.put(step, currentStepMap);
        }

        return fromStepToDataProduce;
    }

}
