package client.user.GUI.body.history;

import javafx.scene.control.Button;

public class ExecutionHistoryRowData {
    private String name;
    private String timeOfExecution;
    private String result;
    private String runBy;
    private Button seeDataButton;

    public ExecutionHistoryRowData(String name, String timeOfExecution, String result, String runBy, Button seeData) {
        this.name = name;
        this.timeOfExecution = timeOfExecution;
        this.result = result;
        this.runBy = runBy;
        this.seeDataButton = seeData;
    }

    public String getName() {
        return name;
    }

    public String getTimeOfExecution() {
        return timeOfExecution;
    }

    public String getResult() {
        return result;
    }

    public Button getSeeDataButton() {
        return seeDataButton;
    }

    public String getRunBy() {
        return runBy;
    }
}
