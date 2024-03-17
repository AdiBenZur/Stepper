package console.operation;

import flow.definition.api.FlowDefinition;
import flow.definition.api.StepUsageDeclaration;
import flow.execution.FlowExecution;
import manager.system.Manager;
import step.api.StepDefinition;
import java.time.Duration;
import java.util.Map;

public class DisplayStatistics {
    private Manager manager;

    public DisplayStatistics(Manager manager) {
        this.manager = manager;
    }

    public void displayExecutionsStatistics() {
        if(manager.getStepsStatisticsManager().size() == 0) {
            System.out.println("No flow process was initiated. \n");
            return;
        }

        System.out.println("Flows statistics:");
        for(FlowDefinition flowDefinition : manager.getStepper().getAllFlows()) {
            String flowName = flowDefinition.getName();
            Integer nofTimeExecute = getNofExecute(flowName);

            // Check if current flow has run
            if(nofTimeExecute != 0) {
                long avg = getAverageFlow(flowName);
                System.out.println("Flow name: " + flowName + " execute " + nofTimeExecute.toString() + " times. Average flow activation time is: " + avg + " millisecond.");
            }
        }

        System.out.println("\nSteps statistics:");
        Map<StepDefinition, Map<StepUsageDeclaration, Duration>> statisticsMap = manager.getStepsStatisticsManager();

        // Run over statistics manager key
        for(StepDefinition stepDefinition : manager.getStepsStatisticsManager().keySet() ) {
            Map<StepUsageDeclaration, Duration> stepMap = manager.getStepsStatisticsManager().get(stepDefinition);
            Integer nofTimeExecute = stepMap.size();
            long stepAvg = getAverageStep(stepMap);
            System.out.println("Step name: " + stepDefinition.getName() + " execute " + nofTimeExecute + " times. Average step activation time is: " + stepAvg + " millisecond.");
        }
        System.out.println();
    }

    public long getAverageFlow(String flowName) {
        long sum = 0;
        Integer counter = 0;

        for(FlowExecution flowExecution : manager.getDataManager().getExecutionData()) {
            if(flowExecution.getFlowDefinition().getName().equals(flowName)) {
                counter ++;
                sum += flowExecution.getTotalTime();
            }
        }
        if(counter == 0)
            return 0;
        else
            return (sum / counter);
    }

    public Integer getNofExecute(String flowName) {
        Integer counter = 0;

        for(FlowExecution flowExecution : manager.getDataManager().getExecutionData()) {
            if(flowExecution.getFlowDefinition().getName().equals(flowName))
                counter ++;
        }
        return counter;
    }


    public long getAverageStep(Map<StepUsageDeclaration, Duration> map) {
        long sum  = 0;
        Integer counter = map.size();

        for(Duration duration : map.values()) {
            sum += duration.toMillis();
        }
        return (sum / counter);
    }

}
