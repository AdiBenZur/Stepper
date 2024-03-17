package dto.step;

public class StepLogDTO {
    private final String stepName;
    private final String log;

    public StepLogDTO(String stepName, String log) {
        this.stepName = stepName;
        this.log = log;
    }

    public String getStepName() {
        return stepName;
    }

    public String getLog() {
        return log;
    }
}
