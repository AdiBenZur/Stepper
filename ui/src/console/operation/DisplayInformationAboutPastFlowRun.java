package console.operation;

import flow.definition.api.StepUsageDeclaration;
import flow.execution.FlowExecution;
import flow.execution.log.StepLog;
import io.api.DataNecessity;
import io.api.IODefinitionData;
import manager.system.Manager;
import java.time.Duration;
import java.util.Map;
import java.util.Scanner;

public class DisplayInformationAboutPastFlowRun {
    private static final int NO_FLOWS_IN_SYSTEM = -1;

    private Manager manager;
    private Scanner scanner;


    public DisplayInformationAboutPastFlowRun(Manager manager) {
        this.manager = manager;
        scanner = new Scanner(System.in);
    }

    public void displayInformationAboutPastRun() {

        int choice = scanRunChoice();

        if(choice == NO_FLOWS_IN_SYSTEM || choice == 0)
            return;

        FlowExecution flowExecution = manager.getDataManager().getExecutionData().get(choice - 1);

        //Print information

        System.out.println("Presenting information about the chosen run : ");
        System.out.println("Unique id: " + flowExecution.getUniqueId());
        System.out.println("Flow run name: " + flowExecution.getFlowName());
        System.out.println("Run result : " + flowExecution.getFlowExecutionStatus());
        System.out.println("Executing duration: " + flowExecution.getTotalTime() + " milliseconds.");

        System.out.println("Flow free inputs: ");
        Map<IODefinitionData, Object> freeInputsMap = flowExecution.getFlowFreeInputs().getFromInputToObject();
        // Print all mandatory
        for(IODefinitionData input : freeInputsMap.keySet()) {
            if(input.getNecessity() == DataNecessity.MANDATORY) {
                printFreeInputsDetails(freeInputsMap, input);
            }
        }
        // Print all optional
        for(IODefinitionData input : freeInputsMap.keySet()) {
            if(input.getNecessity() == DataNecessity.OPTIONAL) {
               printFreeInputsDetails(freeInputsMap, input);
            }
        }

        if(flowExecution.getAllOutputProduceDuringFlow().keySet().isEmpty())
            System.out.println("All outputs produce during run: there is no outputs.");
        else {
            System.out.println("All outputs produce during run:");
            for (IODefinitionData output : flowExecution.getAllOutputProduceDuringFlow().keySet()) {
                System.out.println("\tName: " + output.getName());
                System.out.println("\tType: " + output.getDataDefinition().getType().getSimpleName());
                System.out.println("\tValue: " + flowExecution.getAllOutputProduceDuringFlow().get(output).toString());
                System.out.println();
            }
        }

        System.out.println("Information about the steps participants in run: ");
        for(StepUsageDeclaration step : flowExecution.getStepsInFlow()) {

            // Print name
            if(step.getStepName().equals(step.getStepDefinition().getName()))
                System.out.println("\tStep name: " + step.getStepName());
            else
                System.out.println("\tOriginal step name: " + step.getStepDefinition().getName() + " ,alias: " + step.getStepName());

            // Print duration
            Duration stepDuration = manager.getStepDurationFromStepStatisticsManager(flowExecution, step);
            if(stepDuration == null)
                System.out.println("\tDuration: the flow failed before the step was run.");
            else
                System.out.println("\tDuration: " + stepDuration.toMillis() + " millisecond.");

            // Print result
            try{
                String currentStepResult = step.getStepDefinition().getStepResult();
                System.out.println("\tStep result: " + currentStepResult);

            }
            catch (NullPointerException e) {
                System.out.println("\tStep result: the flow failed before the step was run");
            }


            // Print Summary line
            // There is an options that an error occurred in the end of the step so the list will contain two summary lines for the same step.
            //  Need to print the most updated summary line

            if(!isFlowHasSummaryLine(step.getStepName(), flowExecution))
                System.out.println("\tStep summary line: the step does not executed.");
            else {
                for (int i = flowExecution.getContext().getStepsSummaryLine().size() - 1; i >= 0; i--) {
                    if (flowExecution.getContext().getStepsSummaryLine().get(i).getStepName().equals(step.getStepName())) {
                        System.out.println("\tStep summary line: " + flowExecution.getContext().getStepsSummaryLine().get(i).getSummaryLine());
                        break;
                    }
                }
            }

            // Print logs
            if(!isFlowHasLogs(step.getStepName(), flowExecution))
                System.out.println("\tStep logs: the step does not executed.");
            else {
                System.out.println("\tStep logs:");
                for (StepLog stepLog : flowExecution.getContext().getFlowLogs()) {
                    if (stepLog.getStepName().equals(step.getStepName()))
                        System.out.println("\t\t" + stepLog.getStepLog());
                }
            }
            System.out.println();
        }
    }


    public int scanRunChoice() {

        boolean isInputOk = false;

        if(manager.getDataManager().getExecutionData().size() == 0) {
            System.out.println("There are no flow has been executed in the system yet.\n");
            return NO_FLOWS_IN_SYSTEM;
        }


        String choice;
        Integer choiceInt = null;

        do {
            printRunMenu();
            choice = scanner.nextLine();

            try {
                choiceInt = Integer.parseInt(choice);

                // Check choice in range
                if(choiceInt < 0 || choiceInt > manager.getDataManager().getExecutionData().size()) {
                    throw new NumberFormatException();
                }
                else
                    isInputOk = true;
            }
            catch (NumberFormatException e) {
                System.out.println("Invalid input. Please try again.\n");
                isInputOk = false;
            }

        } while (!isInputOk);

        System.out.println();
        return choiceInt;
    }

    public void printRunMenu() {
        System.out.println("Choose one of the following options or press 0 to exit: ");
        System.out.println("0- Exit");
        for(int i = 0; i < manager.getDataManager().getExecutionData().size(); i ++) {
            FlowExecution currentFlowExecution = manager.getDataManager().getExecutionData().get(i);
            System.out.println((1 + i) + ". Flow name: " + currentFlowExecution.getFlowName());
            System.out.println("   Id: " + currentFlowExecution.getUniqueId());
            System.out.println("   Execute at: " + currentFlowExecution.getStartTimeOfExecution());
        }
    }

    public Boolean isFlowHasLogs(String name, FlowExecution flowExecution) {
        for(StepLog stepLog : flowExecution.getContext().getFlowLogs()) {
            if(stepLog.getStepName().equals(name))
                return true;
        }
        return false;
    }

    public Boolean isFlowHasSummaryLine(String name, FlowExecution flowExecution) {
        for(int i = 0; i < flowExecution.getContext().getStepsSummaryLine().size(); i ++) {
            if (flowExecution.getContext().getStepsSummaryLine().get(i).getStepName().equals(name))
                return true;
        }
        return false;
    }

    public void printFreeInputsDetails(Map<IODefinitionData, Object> freeInputsMap, IODefinitionData input) {
        System.out.println("\tName: " + input.getName());
        System.out.println("\tType: " + input.getDataDefinition().getType().getSimpleName());
        System.out.println("\tValue: " + freeInputsMap.get(input).toString());
        System.out.println("\tNecessity: " + input.getNecessity().toString());
        System.out.println();
    }


}
