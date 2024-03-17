package flow.execution.summaryline;

public class StepSummaryLineImpl implements StepSummaryLine{
    private String stepName;
    private String stepSummaryLine;

    public StepSummaryLineImpl(String stepName, String stepSummaryLine) {
        this.stepName = stepName;
        this.stepSummaryLine = stepSummaryLine;
    }

    @Override
    public String getStepName() {
        return stepName;
    }

    @Override
    public String getSummaryLine() {
        return stepSummaryLine;
    }

    @Override
    public void setSummaryLine(String line) {
        this.stepSummaryLine = line;
    }
}
