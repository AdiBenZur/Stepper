package flow.execution.log;

public interface StepLog {
    String getStepName();
    String getStepLog();
    void setStepLog(String log);
}
