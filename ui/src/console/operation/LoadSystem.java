package console.operation;

import manager.system.Manager;

import java.util.List;
import java.util.Scanner;

public class LoadSystem {
    private Manager manager;
    private Scanner scanner;

    public LoadSystem(Manager manager) {
        this.manager = manager;
        scanner = new Scanner(System.in);
    }

    public void loadSystemFromFile() {
        List<String> errors;

        // Reset data manager
        manager.resetManager();

        // Reset step statistics manager
        manager.resetStepsStatisticsManager();


        // Read path
        System.out.println("Please enter the path to xml file: ");
        String xmlPath = scanner.nextLine();
        System.out.println();


        errors = manager.loadDataFromXml(xmlPath);

        // Check if there are errors while load the system
        if(!errors.isEmpty()) {
            System.out.println("Several errors occurred while loading the system: ");
            for (String error : errors) {
                System.out.println(error);
            }
            resetStepper();
            System.out.println();
        }
        else
            System.out.println("The system has been loaded successfully.\n");
    }

    public void resetStepper() {
        for(int i = 0; i < manager.getStepper().getAllFlows().size(); i ++)
            manager.getStepper().getAllFlows().clear();
    }
}
