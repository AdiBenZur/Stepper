package desktop.application;

import desktop.body.BodyController;
import desktop.header.HeaderController;
import javafx.fxml.FXML;
import javafx.scene.Parent;



public class AppController {

    // Mediate between the header and the body

    @FXML
    private Parent headerComponent;
    @FXML
    private HeaderController headerComponentController;
    @FXML
    private Parent bodyComponent;
    @FXML
    private BodyController bodyComponentController;

    @FXML
    public void initialize() {
        if (headerComponentController != null && bodyComponentController != null) {
            headerComponentController.setAppController(this);
            bodyComponentController.setAppController(this);
        }
    }


    public void flowDefinitionButtonPress() {
        bodyComponentController.flowDefinitionButtonPress();
    }

    public void flowExecutionButtonPress() {
        bodyComponentController.flowExecutionButtonPress(null, null);
    }

    public void executionsHistoryButtonPress() { bodyComponentController.executionsHistoryButtonPress(); }

    public void statisticsButtonPress() { bodyComponentController.statisticsButtonPress(); }

    public void setFlowExecute() {
        headerComponentController.setFlowExecute();
    }

    public void returnToStartScene() {
        bodyComponentController.initialize();
    }


}
