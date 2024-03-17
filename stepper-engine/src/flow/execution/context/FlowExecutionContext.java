package flow.execution.context;

import exception.data.NoInputFoundException;
import exception.data.TypeDontMatchException;
import flow.definition.api.StepUsageDeclaration;
import flow.execution.log.StepLog;
import flow.execution.summaryline.StepSummaryLine;
import io.api.IODefinitionData;

import java.util.List;
import java.util.Map;

public interface FlowExecutionContext {
    // What are the capabilities we need from the infrastructure.

    <T> T getDataValue(String inputName, Class<T> expectedDataType) throws NoInputFoundException, TypeDontMatchException; // Using T only in this method.
    void storeDataValue(String dataName, Object value);
    void storeDataValueFreeInputs(String dataName, Object value);
    void addLog(StepLog log);
    void addStepSummaryLine(StepSummaryLine summaryLine);
    void updateCurrentRunningStep(StepUsageDeclaration newWorker);
    StepUsageDeclaration getCurrentRunningStep();
    void setCurrentStepRunning(StepUsageDeclaration stepUsageDeclaration);
    Map<String, Object> getContextValues();
    List<StepLog> getFlowLogs();
    IODefinitionData getDataByName(String name);
    List<StepSummaryLine> getStepsSummaryLine();
    Map<IODefinitionData, Object> getAllExecutionInputsAndOutputs();

}
