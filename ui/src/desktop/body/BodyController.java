package desktop.body;

import desktop.application.AppController;
import desktop.application.EngineController;
import desktop.body.impl.scene1.FlowsDefinitionController;
import desktop.body.impl.scene2.FlowExecutionController;
import desktop.body.impl.scene3.ExecutionsHistoryController;
import desktop.body.impl.scene4.StatisticsController;
import flow.definition.api.FlowDefinition;
import flow.execution.FlowExecution;
import io.impl.UserFreeInputs;
import javafx.fxml.FXML;
import javafx.scene.Parent;

import javax.jws.soap.SOAPBinding;

public class BodyController {


    @FXML
    private Parent flowDefinitionComponent;
    @FXML
    private FlowsDefinitionController flowDefinitionComponentController;
    @FXML
    private Parent flowExecutionComponent;
    @FXML
    private FlowExecutionController flowExecutionComponentController;
    @FXML
    private Parent executionHistoryComponent;
    @FXML
    private ExecutionsHistoryController executionHistoryComponentController;
    @FXML
    private Parent statisticsComponent;
    @FXML
    private StatisticsController statisticsComponentController;
    @FXML
     private Parent startAppComponent;


    private AppController appController;


    @FXML
    public void initialize() {
        if (flowDefinitionComponentController != null && flowExecutionComponentController != null && executionHistoryComponentController != null && statisticsComponentController != null) {
            flowDefinitionComponentController.setBodyController(this);
            flowExecutionComponentController.setBodyController(this);
            executionHistoryComponentController.setBodyController(this);
            statisticsComponentController.setBodyController(this);
        }

        startAppComponent.setVisible(true);
        flowDefinitionComponent.setVisible(false);
        flowExecutionComponent.setVisible(false);
        executionHistoryComponent.setVisible(false);
        statisticsComponent.setVisible(false);
    }


    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    public void flowDefinitionButtonPress() {
        startAppComponent.setVisible(false);
        flowDefinitionComponent.setVisible(true);
        flowExecutionComponent.setVisible(false);
        executionHistoryComponent.setVisible(false);
        statisticsComponent.setVisible(false);

        flowDefinitionComponentController.fillScene();
    }

    public void flowExecutionButtonPress(FlowDefinition flowChoose, UserFreeInputs userFreeInputs) {
        startAppComponent.setVisible(false);
        flowDefinitionComponent.setVisible(false);
        flowExecutionComponent.setVisible(true);
        executionHistoryComponent.setVisible(false);
        statisticsComponent.setVisible(false);

        flowExecutionComponentController.setReRunExecution(false);
        flowExecutionComponentController.setCurrentFlowRunning(flowChoose);
        flowExecutionComponentController.setUserFreeInputs(userFreeInputs);
        flowExecutionComponentController.flowExecutionButtonPress();
    }

    public void executionsHistoryButtonPress() {
        startAppComponent.setVisible(false);
        flowDefinitionComponent.setVisible(false);
        flowExecutionComponent.setVisible(false);
        executionHistoryComponent.setVisible(true);
        statisticsComponent.setVisible(false);

        executionHistoryComponentController.fillScene();
    }

    public void statisticsButtonPress() {
        startAppComponent.setVisible(false);
        flowDefinitionComponent.setVisible(false);
        flowExecutionComponent.setVisible(false);
        executionHistoryComponent.setVisible(false);
        statisticsComponent.setVisible(true);

        statisticsComponentController.fillScene();
    }

    public void setFlowExecute() {
        appController.setFlowExecute();
    }

    public void isRerunButtonPress(FlowExecution flowExecution) {
        startAppComponent.setVisible(false);
        flowDefinitionComponent.setVisible(false);
        flowExecutionComponent.setVisible(true);
        executionHistoryComponent.setVisible(false);
        statisticsComponent.setVisible(false);

        flowExecutionComponentController.setFlowExecutionToRerun(flowExecution);
        flowExecutionComponentController.setReRunExecution(true);
        FlowDefinition flowDefinition = EngineController.getInstance().getStepper().getFlowDefinitionByName(flowExecution.getFlowDefinition().getName());
        flowExecutionComponentController.setCurrentFlowRunning(flowDefinition);
        flowExecutionComponentController.setUserFreeInputs(flowExecution.getFlowFreeInputs());
        flowExecutionComponentController.flowExecutionButtonPress();
    }

}
