package client.user.GUI.body;

import client.user.GUI.UserAppController;
import client.user.GUI.body.definition.FlowDefinitionController;
import client.user.GUI.body.execution.executiondata.ExecutionDataController;
import client.user.GUI.body.execution.freeinputs.FreeInputsController;
import client.user.GUI.body.history.UserExecutionsHistory;
import dto.io.UserFreeInputsDTO;
import javafx.fxml.FXML;
import javafx.scene.Parent;

public class UserBodyController {
    @FXML
    private Parent flowDefinitionComponent;
    @FXML
    private FlowDefinitionController flowDefinitionComponentController;
    @FXML
    private Parent freeInputsComponent;
    @FXML
    private FreeInputsController freeInputsComponentController;
    @FXML
    private Parent executionDataComponent;
    @FXML
    private ExecutionDataController executionDataComponentController;
    @FXML
    private Parent executionsHistoryComponent;
    @FXML
    private UserExecutionsHistory executionsHistoryComponentController;
    @FXML
    private Parent startSceneComponent;

    private UserAppController userAppController;


    @FXML
    public void initialize() {
        if (flowDefinitionComponentController != null && freeInputsComponentController != null && executionDataComponentController != null && executionsHistoryComponentController != null) {
            flowDefinitionComponentController.setBodyController(this);
            freeInputsComponentController.setBodyController(this);
            executionDataComponentController.setBodyController(this);
            executionsHistoryComponentController.setBodyController(this);
        }
    }


    public void setAppController(UserAppController userAppController) {
        this.userAppController = userAppController;
    }

    public void userJustEnteredToSystem() {
        flowDefinitionComponent.setVisible(false);
        freeInputsComponent.setVisible(false);
        executionDataComponent.setVisible(false);
        executionsHistoryComponent.setVisible(false);
        startSceneComponent.setVisible(true);
    }

    public void flowDefinitionButtonIsPress() {
        flowDefinitionComponent.setVisible(true);
        freeInputsComponent.setVisible(false);
        executionDataComponent.setVisible(false);
        executionsHistoryComponent.setVisible(false);
        startSceneComponent.setVisible(false);

        flowDefinitionComponentController.startScene();
    }

    public void executeFlowButtonPress(UserFreeInputsDTO userFreeInputsDTO) {
        flowDefinitionComponent.setVisible(false);
        freeInputsComponent.setVisible(true);
        executionDataComponent.setVisible(false);
        executionsHistoryComponent.setVisible(false);
        startSceneComponent.setVisible(false);

        freeInputsComponentController.setRerun(false);
        freeInputsComponentController.startScene(userFreeInputsDTO);
    }

    public void startFlowButtonPress(UserFreeInputsDTO userFreeInputsDTO) {
        flowDefinitionComponent.setVisible(false);
        freeInputsComponent.setVisible(false);
        executionDataComponent.setVisible(true);
        executionsHistoryComponent.setVisible(false);
        startSceneComponent.setVisible(false);

        executionDataComponentController.startScene(userFreeInputsDTO);
    }

    public void executionHistoryButtonPress() {
        flowDefinitionComponent.setVisible(false);
        freeInputsComponent.setVisible(false);
        executionDataComponent.setVisible(false);
        executionsHistoryComponent.setVisible(true);
        startSceneComponent.setVisible(false);

        executionsHistoryComponentController.activate();
    }

    public void FlowExecutionButtonPressFromHeader() {
        flowDefinitionComponent.setVisible(false);
        freeInputsComponent.setVisible(true);
        executionDataComponent.setVisible(false);
        executionsHistoryComponent.setVisible(false);
        startSceneComponent.setVisible(false);

        freeInputsComponentController.startScene(null);
    }

    public void isRerunButtonPress(UserFreeInputsDTO userFreeInputsDTO) {
        flowDefinitionComponent.setVisible(false);
        freeInputsComponent.setVisible(true);
        executionDataComponent.setVisible(false);
        executionsHistoryComponent.setVisible(false);
        startSceneComponent.setVisible(false);

        freeInputsComponentController.setRerun(true);
        freeInputsComponentController.startScene(userFreeInputsDTO);
    }
}
