package desktop.body.impl.scene2.executiondata;

import datadefinition.impl.DataDefinitionRegistry;
import desktop.application.EngineController;
import desktop.body.api.SceneBody;
import desktop.body.impl.scene2.FlowExecutionController;
import desktop.component.impl.*;
import flow.definition.api.FlowDefinition;
import flow.definition.api.StepUsageDeclaration;
import flow.execution.FlowExecution;
import flow.execution.log.StepLog;
import io.api.DataNecessity;
import io.api.IODefinitionData;
import io.impl.UserFreeInputs;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.Duration;
import java.util.Map;

public class ExecutionDataController implements SceneBody {

    @FXML
    private Button continuationButton;
    @FXML
    private RadioButton generalFlowDataRadioButton;
    @FXML
    private TitledPane stepsTitledPane;
    @FXML
    private VBox detailsVBox;
    @FXML
    private VBox stepsTitledPaneContentVBox;
    @FXML
    private Accordion accordion;

    private FlowExecutionController flowExecutionController;
    private FlowDefinition currentFlowRunning;
    private UserFreeInputs userFreeInputs;
    private FlowExecution flowExecution;
    private SimpleBooleanProperty isFlowContinuationChoose;
    private FlowDefinition flowToContinueTo;



    public ExecutionDataController() {
        isFlowContinuationChoose = new SimpleBooleanProperty();
    }

    public void setFlowExecutionController(FlowExecutionController flowExecutionController) {
        this.flowExecutionController = flowExecutionController;
    }

    public void setCurrentFlowRunning(FlowDefinition currentFlowRunning) {
        this.currentFlowRunning = currentFlowRunning;
    }


    public void setUserFreeInputs(UserFreeInputs userFreeInputs) {
        this.userFreeInputs = userFreeInputs;
    }


    @Override
    public void fillScene() {
        // Clear data
        stepsTitledPaneContentVBox.getChildren().clear();
        detailsVBox.getChildren().clear();


        // Run the flow and show progress
        flowExecution = EngineController.getInstance().flowExecution(currentFlowRunning, userFreeInputs);

        // Add to data manager
        EngineController.getInstance().addNewDataToDataManager(flowExecution);

        flowExecution.getStepFinishedProperty().addListener((observable, oldValue, newValue) -> {
            try {
                updateExecuteStepProgress();
            }
            catch (Exception e) {
                //e.printStackTrace();
            }
        });

        ToggleGroup toggleGroup = new ToggleGroup();
        generalFlowDataRadioButton.setToggleGroup(toggleGroup);
        generalFlowDataRadioButton.setOnAction(event -> {
            showFlowExecutionDetails();
        });

        // Add steps
        for(int i = 0; i < flowExecution.getStepsInFlow().size(); i ++) {
            StepUsageDeclaration step = flowExecution.getStepsInFlow().get(i);
            RadioButton radioButton = new RadioButton("Step " + (i + 1) + ": " + step.getStepName());
            radioButton.setToggleGroup(toggleGroup);
            radioButton.setOnAction(event -> {
                showStepExecutionDetails(step);
            });
            stepsTitledPaneContentVBox.getChildren().add(radioButton);
        }

        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ((RadioButton) newValue).setStyle("-fx-font-size: 12px; -fx-text-fill: #147e2e;");
            }
            if (oldValue != null) {
                ((RadioButton) oldValue).setStyle("-fx-font-size: 12px; -fx-text-fill: BLACK;");
            }
        });

        accordion.disableProperty().bind(flowExecution.getIsExecutionEndedProperty().not());

    }

    public void updateExecuteStepProgress() {
        Platform.runLater(() -> {
            detailsVBox.getChildren().clear();

            Label title = new Label("Execution Progress");
            title.setStyle("-fx-text-fill: #28104e;");
            title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            detailsVBox.getChildren().add(title);


            for (StepUsageDeclaration step : flowExecution.getStepsInFlow()) {
                try {
                    String currentStepResult = step.getStepDefinition().getStepResult();
                    HBox hBox = new HBox();
                    hBox.setSpacing(3);
                    hBox.getChildren().add(new Label("Step: " + step.getStepName() + "- "));
                    Label result = new Label(currentStepResult);

                    // Set colors
                    if (currentStepResult.equals("Success"))
                        result.setStyle("-fx-text-fill: #1d8348;");
                    if (currentStepResult.equals("Failure"))
                        result.setStyle("-fx-text-fill: #ff0000;");
                    if (currentStepResult.equals("Warning"))
                        result.setStyle("-fx-text-fill: #ff8c00;");

                    hBox.getChildren().add(result);
                    detailsVBox.getChildren().add(hBox);
                } catch (Exception e) {

                }
            }
        });
    }


    public void showFlowExecutionDetails() {
        isFlowContinuationChoose.set(false);

        // Clear previous details
        detailsVBox.getChildren().clear();

        Label flowName = new Label("Flow name: " + currentFlowRunning.getName());
        flowName.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        detailsVBox.getChildren().add(flowName);

        detailsVBox.getChildren().add(new Label("Flow ID: " + flowExecution.getUniqueId()));

        detailsVBox.getChildren().add(new Label("Execute at: " + flowExecution.getStartTimeOfExecution()));

        detailsVBox.getChildren().add(new Label("Duration: " + flowExecution.getTotalTime() + " milliseconds"));

        detailsVBox.getChildren().add(new Label("Result: " + flowExecution.getFlowExecutionStatus()));

        Text flowSummaryLine = new Text("Summary line: " + flowExecution.getFlowSummaryLine());
        flowSummaryLine.setWrappingWidth(380);
        detailsVBox.getChildren().add(flowSummaryLine);

        detailsVBox.getChildren().add(new Label());

        // Steps
        Label steps = new Label("Steps");
        steps.setStyle("-fx-text-fill: #28104e;");
        steps.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        detailsVBox.getChildren().add(steps);
        VBox vBox = new VBox();
        vBox.setSpacing(3);
        for(StepUsageDeclaration step : flowExecution.getStepsInFlow()) {
            try{
                String currentStepResult = step.getStepDefinition().getStepResult();
                HBox hBox = new HBox();
                hBox.setSpacing(3);
                hBox.getChildren().add(new Label("Name: " + step.getStepName() + "- "));
                Label result = new Label(currentStepResult);
                // Set colors
                if(currentStepResult.equals("Success"))
                    result.setStyle("-fx-text-fill: #1d8348;");
                if(currentStepResult.equals("Failure"))
                    result.setStyle("-fx-text-fill: #ff0000;");
                if(currentStepResult.equals("Warning"))
                    result.setStyle("-fx-text-fill: #ff8c00;");

                hBox.getChildren().add(result);
                vBox.getChildren().add(hBox);
            }
            catch (NullPointerException e) {
                vBox.getChildren().add(new Label("Name: " + step.getStepName() + "- Does not executed"));
            }
        }
        detailsVBox.getChildren().add(vBox);

        detailsVBox.getChildren().add(new Label());

        // Free inputs
        Label freeInputs = new Label("Free Inputs Details");
        freeInputs.setStyle("-fx-text-fill: #28104e;");
        freeInputs.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        detailsVBox.getChildren().add(freeInputs);
        Map<IODefinitionData, Object> freeInputsMap = flowExecution.getFlowFreeInputs().getFromInputToObject();
        detailsVBox.getChildren().add(new Label("Mandatory Inputs: "));

        Integer counter = 1;
        for(IODefinitionData input : freeInputsMap.keySet()) {
            if(input.getNecessity() == DataNecessity.MANDATORY) {
                detailsVBox.getChildren().add(showDataDetails(freeInputsMap, input, counter));
                counter ++;
            }
        }

        counter = 1;
        if(isThereOptionalInputs(freeInputsMap)) {
            detailsVBox.getChildren().add(new Label("Optionals Inputs: "));
            for (IODefinitionData input : freeInputsMap.keySet()) {
                if (input.getNecessity() == DataNecessity.OPTIONAL) {
                    detailsVBox.getChildren().add(showDataDetails(freeInputsMap, input, counter));
                    counter ++;
                }
            }
        }
        else
            detailsVBox.getChildren().add(new Label("No optional inputs inserted !"));

        detailsVBox.getChildren().add(new Label());

        // All outputs produce during flow
        Label allOutputs = new Label("Outputs produce during the flow");
        allOutputs.setStyle("-fx-text-fill: #28104e;");
        allOutputs.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        detailsVBox.getChildren().add(allOutputs);
        counter = 1;
        if(!flowExecution.getAllOutputProduceDuringFlow().keySet().isEmpty()) {
            for (IODefinitionData output : flowExecution.getAllOutputProduceDuringFlow().keySet()) {
                detailsVBox.getChildren().add(showDataDetails(flowExecution.getAllOutputProduceDuringFlow(), output, counter));
                counter++;
            }
        }
        else
            detailsVBox.getChildren().add(new Label("No outputs produce!"));
    }


    public Boolean isThereOptionalInputs(Map<IODefinitionData, Object> freeInputsMap) {
        for(IODefinitionData input : freeInputsMap.keySet()) {
            if(input.getNecessity() == DataNecessity.OPTIONAL)
                return true;
        }
        return false;
    }

    public VBox showDataDetails(Map<IODefinitionData, Object> map, IODefinitionData data, Integer counter) {
        VBox dataVbox = new VBox();
        dataVbox.setSpacing(3);

        dataVbox.getChildren().add(new Label(counter + ". Name: " + data.getName()));
        dataVbox.getChildren().add(new Label("   Type: " + data.getDataDefinition().getType().getSimpleName()));
        dataVbox.getChildren().add(new Label("   User string: " + data.getUserString()));


        HBox hBox = new HBox();
        hBox.setSpacing(5);
        hBox.getChildren().add(new Label("   Value: "));
        if(data.getDataDefinition().equals(DataDefinitionRegistry.RELATION)) {
            Button button = new Button("See relation data");
            button.setOnAction(e -> {
                Stage popupStage = new Stage();
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.setTitle("Table View");
                Scene popupScene = new Scene(showComponent(data, map));
                popupStage.setScene(popupScene);
                popupStage.show();
            });
            hBox.getChildren().add(button);
        }
        else {
            hBox.getChildren().add(showComponent(data, map));
        }
        dataVbox.getChildren().add(hBox);

        if(data.getNecessity() != DataNecessity.NA)
            dataVbox.getChildren().add(new Label("   Necessity: " + data.getNecessity().toString()));

        return dataVbox;
    }

    public Parent showComponent(IODefinitionData data, Map<IODefinitionData, Object> map) {

        if(data.getDataDefinition().equals(DataDefinitionRegistry.FILE_LIST)) {
            FileListComponent fileListComponent = new FileListComponent(data, map);
            return fileListComponent.showComponent();
        }

        if(data.getDataDefinition().equals(DataDefinitionRegistry.STRING_LIST)) {
            StringListComponent stringListComponent = new StringListComponent(data, map);
            return stringListComponent.showComponent();
        }

        if(data.getDataDefinition().equals(DataDefinitionRegistry.MAPPING)) {
            MappingComponent mappingComponent = new MappingComponent(data, map);
            return mappingComponent.showComponent();
        }

        if(data.getDataDefinition().equals(DataDefinitionRegistry.RELATION)) {
            RelationComponent relationComponent = new RelationComponent(data, map);
            return relationComponent.showComponent();
        }

        if(data.getDataDefinition().equals(DataDefinitionRegistry.ZIPPER_ENUMERATOR)) {
            ZipperEnumeratorComponent zipperEnumeratorComponent = new ZipperEnumeratorComponent(data, map);
            return zipperEnumeratorComponent.showComponent();
        }

        return new Label(map.get(data).toString());
    }


    public void showStepExecutionDetails(StepUsageDeclaration step) {

        isFlowContinuationChoose.set(false);

        // Clear previous details
        detailsVBox.getChildren().clear();

        Label stepName = new Label("Step Information");
        stepName.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        detailsVBox.getChildren().add(stepName);

        // Add name
        if(step.getStepName().equals(step.getStepDefinition().getName()))
            detailsVBox.getChildren().add(new Label("Name: " + step.getStepName()));
        else {
            detailsVBox.getChildren().add(new Label("Name: " + step.getStepName()));
            detailsVBox.getChildren().add(new Label("Original name: " + step.getStepDefinition().getName()));
        }

        try{
            // Result
            String currentStepResult = step.getStepDefinition().getStepResult();
            HBox hBox = new HBox();
            hBox.setSpacing(3);
            hBox.getChildren().add(new Label("Execute result: "));
            Label result = new Label(currentStepResult);
            // Set colors
            if(currentStepResult.equals("Success"))
                result.setStyle("-fx-text-fill: #1d8348;");
            if(currentStepResult.equals("Failure"))
                result.setStyle("-fx-text-fill: #ff0000;");
            if(currentStepResult.equals("Warning"))
                result.setStyle("-fx-text-fill: #ff8c00;");

            hBox.getChildren().add(result);
            detailsVBox.getChildren().add(hBox);

            // Summery line
            for (int i = flowExecution.getContext().getStepsSummaryLine().size() - 1; i >= 0; i--) {
                if (flowExecution.getContext().getStepsSummaryLine().get(i).getStepName().equals(step.getStepName())) {
                    Text summary = new Text("Summary line: " + flowExecution.getContext().getStepsSummaryLine().get(i).getSummaryLine());
                    summary.setWrappingWidth(400);
                    detailsVBox.getChildren().add(summary);
                    break;
                }
            }

            // Duration
            Duration stepDuration = EngineController.getInstance().getStepDurationFromStepStatisticsManager(flowExecution, step);
            detailsVBox.getChildren().add(new Label("Start time: " + flowExecution.getFromStepToStart(step)));
            detailsVBox.getChildren().add(new Label("End time: " + flowExecution.getFromStepToEnd(step)));
            detailsVBox.getChildren().add(new Label("Duration: " + stepDuration.toMillis() + " ms"));


            // Get all execution inputs and outputs
            Map<StepUsageDeclaration, Map<IODefinitionData, Object>> allInputsAndOutputs = flowExecution.getFromStepToDataMap();
            Map<IODefinitionData, Object> stepMap = allInputsAndOutputs.get(step);

            // Step inputs
            detailsVBox.getChildren().add(new Label());
            Label stepInputs = new Label("Step Inputs");
            stepInputs.setStyle("-fx-text-fill: #28104e;");
            stepInputs.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            detailsVBox.getChildren().add(stepInputs);
            Integer counter = 1;
            for(IODefinitionData data : stepMap.keySet()) {
                if(data.getNecessity() != DataNecessity.NA) {
                    detailsVBox.getChildren().add(showDataDetails(stepMap, data, counter));
                    counter ++;
                }
            }

            // Step outputs
            detailsVBox.getChildren().add(new Label());
            Label stepOutputs = new Label("Step Outputs");
            stepOutputs.setStyle("-fx-text-fill: #28104e;");
            stepOutputs.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            detailsVBox.getChildren().add(stepOutputs);
            counter = 1;
            for(IODefinitionData data : stepMap.keySet()) {
                if(data.getNecessity() == DataNecessity.NA) {
                    detailsVBox.getChildren().add(showDataDetails(stepMap, data, counter));
                    counter ++;
                }
            }

            // Logs
            detailsVBox.getChildren().add(new Label());
            Label stepLogs = new Label("Step Logs");
            stepLogs.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            stepLogs.setStyle("-fx-text-fill: #28104e;");
            detailsVBox.getChildren().add(stepLogs);
            VBox stepLogsVbox = new VBox();
            stepLogsVbox.setSpacing(5);
            for (StepLog stepLog : flowExecution.getContext().getFlowLogs()) {
                if (stepLog.getStepName().equals(step.getStepName())) {
                    Text log = new Text(stepLog.getStepLog());
                    log.setWrappingWidth(400);
                    stepLogsVbox.getChildren().add(log);
                }
            }
            detailsVBox.getChildren().add(stepLogsVbox);
        }
        catch (NullPointerException e) {
            detailsVBox.getChildren().add(new Label("The flow failed before the step was run! "));
        }
    }

    @FXML
    void continuationButtonSetOnAction(ActionEvent event) {
        // Clear previous data
        detailsVBox.getChildren().clear();

        Label stepName = new Label("Possible Continuations");
        stepName.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        detailsVBox.getChildren().add(stepName);
        ToggleGroup toggleGroup = new ToggleGroup();
        if(!currentFlowRunning.getFlowContinuationCustomMapping().isEmpty()) {

            for (FlowDefinition continuation : currentFlowRunning.getFlowContinuationCustomMapping().keySet()) {
                RadioButton radioButton = new RadioButton(continuation.getName());
                radioButton.setToggleGroup(toggleGroup);
                detailsVBox.getChildren().add(radioButton);
            }

            // Create start button
            Button button = new Button("Continue to flow");
            button.setOnAction(e -> {
                UserFreeInputs flowContinuationFreeInputs = EngineController.getInstance().getFlowUserInputsAfterApplyContinuation(flowExecution, userFreeInputs, flowToContinueTo);

                // Apply continuation
                flowExecutionController.setUserFreeInputs(flowContinuationFreeInputs);
                flowExecutionController.setReRunExecution(false);
                flowExecutionController.setCurrentFlowRunning(flowToContinueTo);
                flowExecutionController.flowExecutionButtonPress();
            });

            // Binding
            button.disableProperty().bind(isFlowContinuationChoose.not());

            HBox hBox = new HBox();
            hBox.setPrefHeight(150);
            hBox.setPrefWidth(450);

            hBox.setAlignment(Pos.BOTTOM_RIGHT);
            hBox.setPadding(new Insets(0, 10, 0, 0));
            hBox.getChildren().add(button);
            detailsVBox.getChildren().add(hBox);

            toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                isFlowContinuationChoose.set(true);

                RadioButton selectedRadioButton = (RadioButton) newValue;
                String selectedName = selectedRadioButton.getText();
                flowToContinueTo = EngineController.getInstance().getStepper().getFlowDefinitionByName(selectedName);
            });
        }
        else
            detailsVBox.getChildren().addAll(new Label(), new Label("No continuation has been defined for this flow !"));
    }

}
