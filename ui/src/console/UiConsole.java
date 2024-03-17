package console;

import console.operation.*;
import manager.system.Manager;
import manager.system.ManagerImpl;


import java.util.Scanner;

public class UiConsole {
    private static final String LOAD_SYSTEM = "1";
    private static final String DISPLAY_FLOW_INFORMATION = "2";
    private static final String RUN_FLOW = "3";
    private static final String DISPLAY_INFORMATION_ABOUT_PAST_FLOW_RUN = "4";
    private static final String DISPLAY_SYSTEM_STATISTICS = "5";
    private static final String EXIT = "6";

    private final Manager manager;
    private Boolean isRunning = true;
    private final Scanner scanner;

    public static void main(String[] args) {
        UiConsole uiConsole = new UiConsole(new ManagerImpl());
        while (uiConsole.isRunning)
            uiConsole.displayMenu();
    }


    public UiConsole(Manager manager) {
        this.manager = manager;
        scanner = new Scanner(System.in);
    }


    public void displayMenu() {
        printMenu();

      String choice = scanner.nextLine();
      System.out.println();

        switch (choice) {
            case LOAD_SYSTEM:
                loadSystemFromFile();
                break;

            case DISPLAY_FLOW_INFORMATION:
                displayFlowInformation();
                break;

            case RUN_FLOW:
                runFlow();
                break;

            case DISPLAY_INFORMATION_ABOUT_PAST_FLOW_RUN:
                displayInformationAboutPastFlowRun();
                break;

            case DISPLAY_SYSTEM_STATISTICS:
                displaySystemStatistics();
                break;

            case EXIT:
                exitTheSystem();
                break;

            default:
                System.out.println("Invalid input. Please choose again.\n");
                break;
        }

    }


    public void printMenu() {
        System.out.println("-------- Stepper menu --------");
        System.out.println("Please choose one of the following options: ");
        System.out.println("1- Load system from file.");
        System.out.println("2- Display flow information.");
        System.out.println("3- Run a flow.");
        System.out.println("4- Display information about a past flow run.");
        System.out.println("5- Display system statistics.");
        System.out.println("6- Exit the system.");
    }


    // Command no.1
    public void loadSystemFromFile() {
        LoadSystem loadSystem = new LoadSystem(manager);
        loadSystem.loadSystemFromFile();
    }


    // Command no.2
    public void displayFlowInformation() {
        DisplayFlowInformation displayFlowInformation = new DisplayFlowInformation(manager);
        displayFlowInformation.displayInformation();
    }



    // Command no.3
    public void runFlow() {
        ExecuteFlow executeFlow = new ExecuteFlow(manager);
        executeFlow.executeFlow();
    }

    public void displayInformationAboutPastFlowRun() {
        DisplayInformationAboutPastFlowRun displayPastRun = new DisplayInformationAboutPastFlowRun(manager);
        displayPastRun.displayInformationAboutPastRun();
    }

    public void displaySystemStatistics() {
        DisplayStatistics displayStatistics = new DisplayStatistics(manager);
        displayStatistics.displayExecutionsStatistics();
    }

    public void exitTheSystem() {
        System.out.println("Exit the program. Thanks for using Stepper!");
        isRunning = false;
    }


}
