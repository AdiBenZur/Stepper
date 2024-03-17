package desktop.body.impl.scene2;

import desktop.body.BodyController;
import desktop.body.impl.scene2.executiondata.ExecutionDataController;
import desktop.body.impl.scene2.freeinputs.CollectFreeInputsController;
import flow.definition.api.FlowDefinition;
import flow.execution.FlowExecution;
import io.impl.UserFreeInputs;
import javafx.fxml.FXML;
import javafx.scene.Parent;


public class FlowExecutionController {


    @FXML
    private Parent executeDataComponent;
    @FXML
    private ExecutionDataController executeDataComponentController;
    @FXML
    private Parent freeInputsComponent;
    @FXML
    private CollectFreeInputsController freeInputsComponentController;


    private FlowDefinition currentFlowRunning;
    private FlowExecution flowExecutionToRerun;
    private BodyController bodyController;
    private UserFreeInputs userFreeInputs;
    private Boolean isReRunExecution;


    public FlowExecutionController() {
        isReRunExecution = false;
    }

    public void setReRunExecution(Boolean reRunExecution) {
        isReRunExecution = reRunExecution;
    }

    public void setFlowExecutionToRerun(FlowExecution flowExecutionToRerun) {
        this.flowExecutionToRerun = flowExecutionToRerun;
    }

    @FXML
    private void initialize() {
        if(executeDataComponentController != null && freeInputsComponentController != null) {
            executeDataComponentController.setFlowExecutionController(this);
            freeInputsComponentController.setFlowExecutionController(this);
        }
    }

    public void setBodyController(BodyController bodyController) {
        this.bodyController = bodyController;
    }

    public void setCurrentFlowRunning(FlowDefinition flowDefinition) {
        this.currentFlowRunning = flowDefinition;
    }

    public void setFlowExecute() {
        bodyController.setFlowExecute();
    }

    public void setUserFreeInputs(UserFreeInputs freeInputs) {
        this.userFreeInputs = freeInputs;
    }


    public void flowExecutionButtonPress() {
        // Move to collect free inputs scene
        freeInputsComponentController.setReRunExecution(isReRunExecution);
        if(isReRunExecution)
            freeInputsComponentController.setFlowExecutionToRerun(flowExecutionToRerun);

        freeInputsComponentController.setUserFreeInputs(userFreeInputs);
        freeInputsComponentController.setFlow(currentFlowRunning);
        executeDataComponent.setVisible(false);
        freeInputsComponent.setVisible(true);
        freeInputsComponentController.fillScene();
    }


    public void startFlowButtonPress() {
        // Move to flow execution data scene
        executeDataComponentController.setCurrentFlowRunning(currentFlowRunning);
        executeDataComponentController.setUserFreeInputs(userFreeInputs);
        freeInputsComponent.setVisible(false);
        executeDataComponent.setVisible(true);
        executeDataComponentController.fillScene(); // Run flow and show execute data

    }


}
