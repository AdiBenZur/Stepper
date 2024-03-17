package console.operation;

import flow.definition.api.StepUsageDeclaration;
import manager.system.Manager;
import manager.system.operation.FlowInformation;

import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public class DisplayFlowInformation {
    private static final int NO_FLOWS_IN_SYSTEM = -1;

    private final Manager manager;
    private Scanner scanner;

    public DisplayFlowInformation(Manager manager) {
        this.manager = manager;
        scanner = new Scanner(System.in);
    }

    public void displayInformation() {
        List<String> flowNames = manager.getFlowNamesList();
        int choice = scanFlowChoice(); // The input is valid

        if(choice == 0)
            return;

        if(choice != NO_FLOWS_IN_SYSTEM) {
            FlowInformation flowInformation = manager.showFlowInformation(manager.getFlowNamesList().get(choice - 1));
            printFlowInformation(flowInformation);
        }

    }


    public void printFlowInformation(FlowInformation flow) {
        System.out.println();
        System.out.println("Flow information: ");

        // Print Flow's name
        System.out.println("Name: " + flow.getName());

        // Print flow's description
        System.out.println("Description: " + flow.getFlowDescription());

        // Print flow's formal outputs
        System.out.println("Formal outputs:");
        flow.getFormalOutputs().stream().map(data -> "\t" + data.getName()).forEach(System.out::println);

        // Print if the flow is read only or not
        System.out.println("Read only: " + flow.getRadOnly());

        // Print step's data
        System.out.println("Steps in flow: ");
        for(int i = 0; i < flow.getSteps().size(); i ++) {
            String alias = flow.getSteps().get(i).getStepName();
            String original = flow.getSteps().get(i).getStepDefinition().getName();
            String stepName;
            if(alias.equals(original))
                stepName = "Name: " + alias;
            else
                stepName = "Original name: " + original + ", alias: " + alias;
            System.out.println("\t" + stepName);
            System.out.println("\tRead only: " + flow.getSteps().get(i).getStepDefinition().isReadOnly());
            System.out.println();
        }

        // Print flow's free inputs
        System.out.println("Flow's free inputs: ");
        IntStream.range(0, flow.getFreeInputs().size()).forEach(i -> {
            System.out.println("\t" + "Name: " + flow.getFreeInputs().get(i).getName());
            System.out.println("\tType: " + flow.getFreeInputs().get(i).getDataDefinition().getName());
            System.out.println("\tStep names that linked to the input: ");
            List<StepUsageDeclaration> likedSteps = flow.getLinkedStepsByInputKey(flow.getFreeInputs().get(i));
            for (StepUsageDeclaration step : likedSteps) {
                System.out.println("\t\t" + step.getStepName());
            }
            System.out.println("\tThe input is: " + flow.getFreeInputs().get(i).getNecessity().toString());
            System.out.println();
        });

        // Print all outputs produce during flow
        System.out.println("Outputs produce during flow: ");
        IntStream.range(0, flow.getAllOutputs().size()).forEach(i -> {
            System.out.println("\tName: " + flow.getAllOutputs().get(i).getName());
            System.out.println("\tType: " + flow.getAllOutputs().get(i).getDataDefinition().getName());
            System.out.println("\tStep's name that produce this output: " + flow.getStepProduceByKey(flow.getAllOutputs().get(i)).getStepName());
            System.out.println();
        });
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
        System.out.println("Choose one of the following flows: ");
        System.out.println("0- return to menu.");
        for(int i = 0; i < flowNames.size(); i ++) {
            System.out.println((i + 1)  +"- " + flowNames.get(i));
        }
    }

}
