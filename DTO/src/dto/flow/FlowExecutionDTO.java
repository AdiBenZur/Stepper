package dto.flow;

import dto.io.IODataValueDTO;
import dto.step.StepExecutionDTO;
import dto.step.StepLogDTO;

import java.util.List;

public class FlowExecutionDTO {
    private final String flowName;
    private final String uniqueId;
    private final String startTimeOfExecution;
    private final long duration;
    private final String executionResult;
    private final String flowSummaryLine;
    private final List<StepExecutionDTO> steps;
    private final List<IODataValueDTO> freeInputsValues;
    private final List<IODataValueDTO> allOutputProduceDuringFlow;
    private final boolean isProcessing;
    private final List<StepLogDTO> stepLogs;
    private final List<String> stepNames;


    public FlowExecutionDTO(String flowName, String uniqueId, String startTimeOfExecution, long duration, String executionResult, String flowSummaryLine,
                            List<StepExecutionDTO> steps, List<IODataValueDTO> freeInputsValues, List<IODataValueDTO> allOutputProduceDuringFlow, boolean isProcessing,
                            List<StepLogDTO> stepLogs, List<String> stepNames) {
        this.flowName = flowName;
        this.uniqueId = uniqueId;
        this.startTimeOfExecution = startTimeOfExecution;
        this.duration = duration;
        this.executionResult = executionResult;
        this.flowSummaryLine = flowSummaryLine;
        this.steps = steps;
        this.freeInputsValues = freeInputsValues;
        this.allOutputProduceDuringFlow = allOutputProduceDuringFlow;
        this.isProcessing = isProcessing;
        this.stepLogs = stepLogs;
        this.stepNames = stepNames;
    }

    public List<StepLogDTO> getStepLogs() {
        return stepLogs;
    }

    public String getFlowName() {
        return flowName;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getStartTimeOfExecution() {
        return startTimeOfExecution;
    }

    public long getDuration() {
        return duration;
    }

    public String getExecutionResult() {
        return executionResult;
    }

    public String getFlowSummaryLine() {
        return flowSummaryLine;
    }

    public List<StepExecutionDTO> getSteps() {
        return steps;
    }

    public List<IODataValueDTO> getFreeInputsValues() {
        return freeInputsValues;
    }

    public List<IODataValueDTO> getAllOutputProduceDuringFlow() {
        return allOutputProduceDuringFlow;
    }

    public List<String> getStepNames() {
        return stepNames;
    }

    public boolean isProcessing() {
        return isProcessing;
    }
}


