package manager.system.executing.data.api;

import flow.execution.FlowExecution;

import java.util.List;

public interface ExecutingDataManager {
    List<FlowExecution> getExecutionData();
    void addToDataManager(FlowExecution flowExecution);
    void resetManager();
    FlowExecution returnExecutionByUUID(String uuid);
}
