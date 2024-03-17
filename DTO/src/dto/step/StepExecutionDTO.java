package dto.step;


import dto.io.IODataValueDTO;
import flow.execution.log.StepLog;

import java.util.List;

public class StepExecutionDTO {
    private final String name;
    private final String originalName;
    private final String result;
    private final String startTime;
    private final String endTime;
    private final long duration;
    private final List<IODataValueDTO> stepDataValues;
    private final String summaryLine;

    public StepExecutionDTO(String name, String originalName, String result, String startTime, String endTime, long duration, List<IODataValueDTO> stepDataValues, String summaryLine) {
        this.name = name;
        this.originalName = originalName;
        this.result = result;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.stepDataValues = stepDataValues;
        this.summaryLine = summaryLine;
    }

    public List<IODataValueDTO> getStepDataValues() {
        return stepDataValues;
    }

    public String getSummaryLine() {
        return summaryLine;
    }

    public String getName() {
        return name;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getResult() {
        return result;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public long getDuration() {
        return duration;
    }

    public List<IODataValueDTO> getFromDataToValue() {
        return stepDataValues;
    }
}
