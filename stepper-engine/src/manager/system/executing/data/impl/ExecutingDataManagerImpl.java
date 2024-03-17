package manager.system.executing.data.impl;

import flow.execution.FlowExecution;
import manager.system.executing.data.api.ExecutingDataManager;

import java.util.ArrayList;
import java.util.List;

public class ExecutingDataManagerImpl implements ExecutingDataManager {
    // Save all flows run

    List<FlowExecution> executingData;

    public ExecutingDataManagerImpl() {
        executingData = new ArrayList<>();
    }

    @Override
    public List<FlowExecution> getExecutionData() {
        return executingData;
    }

    @Override
    public void addToDataManager(FlowExecution flowExecution) {
        executingData.add(flowExecution);
    }

    @Override
    public void resetManager() {
       executingData.clear();
    }

    @Override
    public FlowExecution returnExecutionByUUID(String uuid) {
        for(FlowExecution execution : executingData) {
            if(execution.getUniqueId().equals(uuid))
                return execution;
        }
        return null;
    }
}
