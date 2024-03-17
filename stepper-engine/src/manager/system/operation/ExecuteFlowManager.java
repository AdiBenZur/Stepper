package manager.system.operation;

import flow.execution.FlowExecution;
import flow.execution.runner.FlowExecutor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecuteFlowManager {
    private ExecutorService threadExecutor = Executors.newFixedThreadPool(1);
    private int numberOfThreads = 1;

    public ExecuteFlowManager() { }


    public void executeFlow(FlowExecution flowExecution) {
        FlowExecutor flowExecutor = new FlowExecutor(flowExecution);
        threadExecutor.execute(flowExecutor);

    }

    public void setThreadPool(int nofThreads) {
        numberOfThreads = nofThreads;
        threadExecutor = Executors.newFixedThreadPool(numberOfThreads);
    }

    public void shutDown() {
        threadExecutor.shutdown();
    }


}
