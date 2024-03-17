package console.operation;

import exception.data.TypeDontMatchException;
import flow.definition.api.FlowDefinition;
import flow.execution.FlowExecution;
import io.api.IODefinitionData;
import io.impl.UserFreeInputs;
import manager.system.Manager;

import java.util.List;
import java.util.Scanner;

public class ExecuteFlow {

    private static final int NO_FLOWS_IN_SYSTEM = -1;
    private Manager manager;
    private Scanner scanner;

    public ExecuteFlow(Manager manager) {
        this.manager = manager;
        scanner = new Scanner(System.in);
    }

    public void executeFlow() {
        // Get flow's inputs
        int choice = scanFlowChoice();

        if(choice == NO_FLOWS_IN_SYSTEM || choice == 0)
            return;


        String chosenFlowName = manager.getFlowNamesList().get(choice - 1);
        FlowDefinition flowDefinition = manager.getStepper().getFlowDefinitionByName(chosenFlowName);
        UserFreeInputs freeInputs = manager.getFlowUserInputs(flowDefinition);

        Integer result = scanInputs(freeInputs);

        // If the user want to exit
        if(result == 0)
            return;

        // Start to execute flow
        System.out.println("\nStart to execute flow " + flowDefinition.getName());
        FlowExecution flowExecution = manager.flowExecution(flowDefinition, freeInputs);

        // Add to data manager
        manager.addNewDataToDataManager(flowExecution);

        // Add to step statistics manager
        manager.addStepsStatisticsFromCurrentFlow(flowExecution);

        // Print data
        System.out.println("End execute flow\n");
        System.out.println("Flow running info: ");
        System.out.println("Flow run id: " + flowExecution.getUniqueId());
        System.out.println("Flow name: " + flowExecution.getFlowDefinition().getName());
        System.out.println("Flow run result: " + flowExecution.getFlowExecutionStatus());
        System.out.println("Flow formal outputs produce during flow: ");
        for(IODefinitionData output : flowDefinition.getFlowFormalOutput()) {
            Object obj = getFormalOutput(flowExecution, output);

            // Check if found
            if (obj == null)
                System.out.println("\t" + output.getUserString() + "- The value doesn't exist due to run error in flow (the flow failed before the output was produced).");
            else {
                System.out.println("\t" + output.getUserString() + "- ");
                System.out.println("\t" + obj.toString());
            }
        }
        System.out.println();
    }


    public Object getFormalOutput(FlowExecution flowExecution, IODefinitionData data) {
        Object obj = null;
        try {
            obj = flowExecution.getAllOutputProduceDuringFlow().get(data);
        }
        catch (Exception e) {
            obj = null;
        }
        return obj;
    }


    public int scanFlowChoice() {
        List<String> flowNames = manager.getFlowNamesList();
        Boolean isInputOk = false;
        String choice;
        Integer choiceInt = null;

        if(flowNames.isEmpty()) {
            System.out.println("There are no flows in the system.\n");
            return NO_FLOWS_IN_SYSTEM;
        }

        do {
            printFlowsMenu(flowNames);
            choice = scanner.nextLine();

            // Check if the input is in range
            try {
                choiceInt = Integer.parseInt(choice);
                if (choiceInt < 0 || choiceInt > flowNames.size()) {
                    throw new NumberFormatException();
                }
                else
                    isInputOk = true;
            }
            catch (NumberFormatException e) {
                System.out.println("Invalid input. Please try again.\n");
                isInputOk = false;
            }
        }while (!isInputOk);

        return choiceInt;
    }


    public void printFlowsMenu(List<String> flowNames) {
        System.out.println("Choose one of the following flows or press 0 to exit: ");
        System.out.println("0- Exit.");
        for(int i = 0; i < flowNames.size(); i ++) {
            System.out.println((i + 1)  +"- " + flowNames.get(i));
        }
    }


    public Integer scanInputs(UserFreeInputs inputs) {
        boolean isUserWantToRunFlow = false;
        String choice = null;
        Integer choiceInt;

        // Show input manu
        while (!isUserWantToRunFlow) {
            printUserFreeInputs(inputs);


            do {
                choice = scanner.nextLine();

                try {
                    choiceInt = Integer.parseInt(choice);

                    // Check if the user want to run flow
                    if (inputs.isAllMandatoryInsert() && choiceInt == inputs.getFreeInputs().size() + 1)
                        isUserWantToRunFlow = true;
                    else {
                        if (choiceInt < 0 || choiceInt > inputs.getFreeInputs().size())
                            throw new NumberFormatException();
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please try again.");
                    choiceInt = null;

                    // If invalid print menu again
                    printUserFreeInputs(inputs);
                }
            } while (choiceInt == null);

            // Here the input is valid

            // Check if the user want to exit
            if (choiceInt == 0)
                return 0;

            // If the user want to scan an input
            if (!isUserWantToRunFlow) {
                System.out.println("Please enter data: ");
                choice = scanner.nextLine();
                try {
                    inputs.scanInput(inputs.getFreeInputs().get(choiceInt - 1), choice);
                } catch (TypeDontMatchException e) {
                    System.out.println("Invalid input. The type does not match the required type. ");
                }
            }
        }

        return 1; // The user doesn't want to exist
    }


    public void printUserFreeInputs(UserFreeInputs inputs) {
        System.out.println("\nInsert data for flow's free inputs. Choose the input you want to scan or press 0 to exit:");
        for(int i = 0; i < inputs.getFreeInputs().size(); i ++) {
            System.out.println((i + 1) + ". Input for step " + inputs.findStepByInputKey(inputs.getFreeInputs().get(i)).getStepName() + "- "
                    + inputs.getFreeInputs().get(i).getUserString() + ", necessity: " + inputs.getFreeInputs().get(i).getNecessity().toString());
        }

        // if all mandatory inserted, show the option to execute flow
        if(inputs.isAllMandatoryInsert())
            System.out.println((inputs.getFreeInputs().size() + 1) + ". Run the flow");
    }

}
