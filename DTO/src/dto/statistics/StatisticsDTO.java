package dto.statistics;

import java.util.List;

public class StatisticsDTO {

    private List<StatisticsDetailsDTO> flows;
    private List<StatisticsDetailsDTO> steps;


    public StatisticsDTO(List<StatisticsDetailsDTO> flows, List<StatisticsDetailsDTO> steps) {
        this.flows = flows;
        this.steps = steps;
    }

    public List<StatisticsDetailsDTO> getFlows() {
        return flows;
    }

    public List<StatisticsDetailsDTO> getSteps() {
        return steps;
    }
}
