package flow.execution.log;

public class StepLogImpl implements StepLog{
    private String stepName;
    private String log;

    public StepLogImpl(String stepName, String log) {
        this.stepName = stepName;
        this.log = log;
    }


    @Override
    public String getStepName() {
        return stepName;
    }

    @Override
    public String getStepLog() {
        return log;
    }

    @Override
    public void setStepLog(String log) {
        this.log = log;
    }


}
