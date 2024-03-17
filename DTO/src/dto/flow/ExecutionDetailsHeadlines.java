package dto.flow;

public class ExecutionDetailsHeadlines {
    private String flowName;
    private String startTimeOfExecutions;
    private String result;
    private final String uuid;
    private String runBy;

    public ExecutionDetailsHeadlines(String flowName, String startTimeOfExecutions, String result, String uuid, String runBy) {
        this.flowName = flowName;
        this.startTimeOfExecutions = startTimeOfExecutions;
        this.result = result;
        this.uuid = uuid;
        this.runBy = runBy;
    }

    public String getFlowName() {
        return flowName;
    }

    public String getStartTimeOfExecutions() {
        return startTimeOfExecutions;
    }

    public String getResult() {
        return result;
    }

    public String getUuid() {
        return uuid;
    }

    public String getRunBy() {
        return runBy;
    }
}
