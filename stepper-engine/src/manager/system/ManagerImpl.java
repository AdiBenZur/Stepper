package manager.system;


import flow.definition.api.FlowDefinition;
import flow.definition.api.StepUsageDeclaration;
import flow.execution.FlowExecution;
import io.api.IODefinitionData;
import io.impl.UserFreeInputs;
import manager.system.executing.data.api.ExecutingDataManager;
import manager.system.executing.data.impl.ExecutingDataManagerImpl;
import manager.system.operation.ExecuteFlowManager;
import manager.system.operation.FlowInformation;
import manager.system.operation.LoaderManager;
import manager.system.operation.LoaderManagerWhileSavingPreviousSystemFlows;
import step.api.StepDefinition;
import stepper.Stepper;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagerImpl implements Manager{
    private Stepper stepper;
    private ExecutingDataManager executingDataManager; // Save past executions
    private Map<StepDefinition, Map<StepUsageDeclaration, Duration>> stepsStatisticManager; // Save all steps statistics, only for console!!!!!!!!
    private ExecuteFlowManager executeFlowManager;


    public ManagerImpl() {
        this.executingDataManager = new ExecutingDataManagerImpl();
        this.stepper = new Stepper();
        stepsStatisticManager = new HashMap<>();
        this.executeFlowManager = new ExecuteFlowManager();
    }


    // For exercise 1 + 2: each file loaded overwrites the previous one!
    @Override
    public List<String> loadDataFromXml(String path) {
        LoaderManager loader = new LoaderManager(stepper);
        List<String> errors = loader.readDataFromXml(path);
        return errors; // to Ui
    }

    // For exercise 3: each file flows are added to previous one!
    @Override
    public List<String> loadDataFromXmlWhileSavingPreviousSystemFlows(String xmlContent) {
        LoaderManagerWhileSavingPreviousSystemFlows loader = new LoaderManagerWhileSavingPreviousSystemFlows(stepper);
        List<String> errors = loader.readDataFromXml(xmlContent);
        return errors; // to server
    }


    @Override
    public FlowInformation showFlowInformation(String flowName) {
        // send to ui- flow information object
        // Get flow object by its name
        for(FlowDefinition flowDefinition : stepper.getAllFlows()) {
            if(flowDefinition.getName().equals(flowName))
                return flowDefinition.getFlowInformation();
        }
        return null;
    }


    @Override
    public FlowExecution flowExecution(FlowDefinition flowDefinition, UserFreeInputs freeInputs) {
        FlowExecution flowExecution = new FlowExecution(flowDefinition, freeInputs);
        executeFlowManager.executeFlow(flowExecution);

        return flowExecution;
    }




    @Override
    public List<String> getFlowNamesList() {
        List<String> names = new ArrayList<>();
        for(int i = 0; i < stepper.getAllFlows().size(); i ++) {
            names.add( stepper.getAllFlows().get(i).getName());
        }
        return names;
    }

    @Override
    public Stepper getStepper() {
        return stepper;
    }

    @Override
    public UserFreeInputs getFlowUserInputs(FlowDefinition flowDefinition) {
        List<IODefinitionData> freeInputs = new ArrayList<>();
        freeInputs.addAll(flowDefinition.getMandatoryInputs());
        freeInputs.addAll(flowDefinition.getOptionalInputs());

        Map<IODefinitionData, StepUsageDeclaration> fromInputToStep = new HashMap<>();
        freeInputs.forEach(data -> {
            fromInputToStep.put(data, flowDefinition.fromMandatoryInputKeyToStep(data));
        });

        return new UserFreeInputs(freeInputs, fromInputToStep, flowDefinition.getInitValues());
    }

    @Override
    public UserFreeInputs getFlowUserInputsAfterApplyContinuation(FlowExecution previousFlow, UserFreeInputs previousUserFreeInputs, FlowDefinition flowToExecute) {
        List<IODefinitionData> freeInputs = new ArrayList<>();
        freeInputs.addAll(flowToExecute.getMandatoryInputs());
        freeInputs.addAll(flowToExecute.getOptionalInputs());

        Map<IODefinitionData, StepUsageDeclaration> fromInputToStep = new HashMap<>();
        freeInputs.forEach(data -> {
            fromInputToStep.put(data, flowToExecute.fromMandatoryInputKeyToStep(data));
        });

        UserFreeInputs userFreeInputs = new UserFreeInputs(freeInputs, fromInputToStep, flowToExecute.getInitValues());

        Map<IODefinitionData, IODefinitionData> continuationCustomMapping = previousFlow.getFlowDefinition().getFlowContinuationCustomMapping().get(flowToExecute);
        List<IODefinitionData> freeInputsPreviousFlow = previousUserFreeInputs.getFreeInputs();
        Map<IODefinitionData, Object> fromInputToObjectPreviousFlow = previousUserFreeInputs.getFromInputToObject();

        for (IODefinitionData previousVal : continuationCustomMapping.keySet()) {
            // Get the value
            Object val = previousFlow.getContext().getContextValues().get(previousVal.getName());
            if (val != null) {

                // Store the data
                try {
                    userFreeInputs.scanInput(continuationCustomMapping.get(previousVal), val.toString());
                }
                catch (Exception e) {

                }
            }
        }

        return userFreeInputs;
    }


    @Override
    public void addNewDataToDataManager(FlowExecution flowExecution) {
        executingDataManager.addToDataManager(flowExecution);
    }

    @Override
    public void resetManager() {
        executingDataManager.resetManager();
    }

    @Override
    public ExecutingDataManager getDataManager() {
        return executingDataManager;
    }

    @Override
    public void addStepsStatisticsFromCurrentFlow(FlowExecution flowExecution) {
        // Only for console !!!!!

        Map<StepDefinition, Map<StepUsageDeclaration, Duration>> stepsStatistics = flowExecution.getStatistics();
        // Iterate over the key-value pairs in stepsStatistics
        for (Map.Entry<StepDefinition, Map<StepUsageDeclaration, Duration>> outerEntry : stepsStatistics.entrySet()) {
            StepDefinition stepDefinition = outerEntry.getKey();
            Map<StepUsageDeclaration, Duration> innerMapToAdd = outerEntry.getValue();

            // Check if the StepDefinition already exists in the statistics manager map
            if (stepsStatisticManager.containsKey(stepDefinition)) {

                // If it does, add the new entries to the inner map
                Map<StepUsageDeclaration, Duration> innerMap = stepsStatisticManager.get(stepDefinition);
                innerMap.putAll(innerMapToAdd);
            } else {

                // If it doesn't, add the new entry to the outer map
                stepsStatisticManager.put(stepDefinition, innerMapToAdd);
            }
        }
    }

    @Override
    public void resetStepsStatisticsManager() {
        stepsStatisticManager.clear();
    }

    @Override
    public Map<StepDefinition, Map<StepUsageDeclaration, Duration>> getStepsStatisticsManager() {
        return stepsStatisticManager;
    }

    @Override
    public Duration getStepDurationFromStepStatisticsManager(FlowExecution flowExecution, StepUsageDeclaration stepUsageDeclaration) {
        return flowExecution.getFromStepToDuration().get(stepUsageDeclaration);
    }


    @Override
    public ExecuteFlowManager getExecuteFlowManager() {
        return executeFlowManager;
    }

    @Override
    public void setThreadPool() {
        executeFlowManager.setThreadPool(stepper.getNumberOfThreads());
    }

    @Override
    public FlowDefinition getFlowDefinitionByName(String name) {
        for(FlowDefinition flow : stepper.getAllFlows()) {
            if(flow.getName().equals(name))
                return flow;
        }

        return null;
    }


}
