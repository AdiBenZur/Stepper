package desktop.body.impl.scene3;


import javafx.scene.control.Button;

public class HistoryRowData {

    private String name;
    private String timeOfExecution;
    private String result;
    private Button seeDataButton;

    public HistoryRowData(String name, String timeOfExecution, String result, Button seeData) {
        this.name = name;
        this.timeOfExecution = timeOfExecution;
        this.result = result;
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
}
