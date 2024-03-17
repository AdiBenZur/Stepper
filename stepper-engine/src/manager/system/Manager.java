package manager.system;

import flow.definition.api.FlowDefinition;
import flow.definition.api.StepUsageDeclaration;
import flow.execution.FlowExecution;
import io.impl.UserFreeInputs;
import manager.system.executing.data.api.ExecutingDataManager;
import manager.system.operation.ExecuteFlowManager;
import manager.system.operation.FlowInformation;
import step.api.StepDefinition;
import stepper.Stepper;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public interface Manager {

    List<String> loadDataFromXml(String path);
    List<String> loadDataFromXmlWhileSavingPreviousSystemFlows(String xmlContent);
    FlowInformation showFlowInformation(String flowName);
    FlowExecution flowExecution(FlowDefinition flowDefinition, UserFreeInputs freeInputs);
    List<String> getFlowNamesList();
    Stepper getStepper();
    UserFreeInputs getFlowUserInputs(FlowDefinition flowDefinition);
    UserFreeInputs getFlowUserInputsAfterApplyContinuation (FlowExecution previousFlow, UserFreeInputs previousUserFreeInputs, FlowDefinition flowToExecute);
    void addNewDataToDataManager(FlowExecution flowExecution);
    void resetManager();
    ExecutingDataManager getDataManager();
    void addStepsStatisticsFromCurrentFlow(FlowExecution flowExecution);
    void resetStepsStatisticsManager();
    Map<StepDefinition, Map<StepUsageDeclaration, Duration>> getStepsStatisticsManager();
    Duration getStepDurationFromStepStatisticsManager(FlowExecution flowExecution, StepUsageDeclaration stepUsageDeclaration);
    ExecuteFlowManager getExecuteFlowManager();
    void setThreadPool();
    FlowDefinition getFlowDefinitionByName(String name);

}
