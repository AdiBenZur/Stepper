package desktop.body.impl.scene3;

import datadefinition.impl.DataDefinitionRegistry;
import desktop.application.EngineController;
import desktop.body.BodyController;
import desktop.body.api.SceneBody;
import desktop.component.impl.*;
import flow.definition.api.StepUsageDeclaration;
import flow.execution.FlowExecution;
import flow.execution.log.StepLog;
import io.api.DataNecessity;
import io.api.IODefinitionData;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.time.Duration;
import java.util.List;
import java.util.Map;


public class ExecutionsHistoryController implements SceneBody {

    @FXML
    private TableView<HistoryRowData> historyTableView;
    @FXML
    private TableColumn<HistoryRowData, String> flowNameColumn;
    @FXML
    private TableColumn<HistoryRowData, String> timeOfExecutionColumn;
    @FXML
    private TableColumn<HistoryRowData, String> resultColumn;
    @FXML
    private TableColumn<HistoryRowData, Button> seeDetailsColumn;
    @FXML
    private ChoiceBox<String > filterChoiceBox;
    @FXML
    private GridPane detailGridPane;
    @FXML
    private VBox stepsTitledPaneContentVBox;
    @FXML
    private VBox detailsVBox;
    @FXML
    private RadioButton generalFlowDataRadioButton;
    @FXML
    private TitledPane executionDataTitledPane;
    @FXML
    private Button rerunFlowButton;

    private BodyController bodyController;
    private SimpleBooleanProperty isFlowSeeDetailsChoose;
    private FlowExecution executionChoose;


    public ExecutionsHistoryController() {
        isFlowSeeDetailsChoose = new SimpleBooleanProperty(false);
    }

    @FXML
    public void initialize() {
        detailGridPane.disableProperty().bind(isFlowSeeDetailsChoose.not());
        
        flowNameColumn.setCellValueFactory(new PropertyValueFactory<HistoryRowData, String>("name"));
        timeOfExecutionColumn.setCellValueFactory(new PropertyValueFactory<HistoryRowData, String>("timeOfExecution"));
        resultColumn.setCellValueFactory(new PropertyValueFactory<HistoryRowData, String>("result"));
        seeDetailsColumn.setCellValueFactory(new PropertyValueFactory<HistoryRowData, Button>("seeDataButton"));

        filterChoiceBox.getItems().addAll("ALL", "Success", "Warning", "Failure");
        filterChoiceBox.setValue("ALL");

        // Add listener when filter changed
        filterChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Call the population methods to apply the filtering
            fillTableView();
        });
    }

    public void setBodyController(BodyController bodyController) {
        this.bodyController = bodyController;
    }


    @Override
    public void fillScene() {
        fillTableView();
    }

    public void fillTableView() {
        rerunFlowButton.setVisible(false);
        ObservableList<HistoryRowData> data = FXCollections.observableArrayList();
        String filterOption = filterChoiceBox.getValue();


        List<FlowExecution> allExecutions = EngineController.getInstance().getDataManager().getExecutionData();
        for(FlowExecution flowExecution : allExecutions) {

            String name = flowExecution.getFlowName();
            String time = flowExecution.getStartTimeOfExecution();
            String result = flowExecution.getFlowExecutionStatus();
            Button button = new Button("Execution Details");

            button.setOnAction(e -> {
                isFlowSeeDetailsChoose.set(true);
                rerunFlowButton.setVisible(true);
                executionChoose = flowExecution;

                fillInformationGridPane();
            });

            if(filterOption.equals("ALL") || flowExecution.getFlowExecutionStatus().equals(filterOption)) {
                HistoryRowData historyRowData = new HistoryRowData(name, time, result, button);
                data.add(historyRowData);
            }
        }

        historyTableView.setItems(data);
    }

    @FXML
    void rerunFlowButtonSetOnAction(ActionEvent event) {
        bodyController.isRerunButtonPress(executionChoose);
    }


    public void fillInformationGridPane() {
        // Clear data
        stepsTitledPaneContentVBox.getChildren().clear();

        ToggleGroup toggleGroup = new ToggleGroup();
        generalFlowDataRadioButton.setToggleGroup(toggleGroup);
        generalFlowDataRadioButton.setOnAction(event -> {
            showFlowExecutionDetails();
        });

        // Add steps
        for(int i = 0; i < executionChoose.getStepsInFlow().size(); i ++) {
            StepUsageDeclaration step = executionChoose.getStepsInFlow().get(i);
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

        // Automatic choose general flow details
        toggleGroup.selectToggle(generalFlowDataRadioButton);
        showFlowExecutionDetails();

    }

    public void showFlowExecutionDetails() {
        // Clear previous details
        detailsVBox.getChildren().clear();

        Label flowName = new Label("Flow name: " + executionChoose.getFlowName());
        flowName.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        detailsVBox.getChildren().add(flowName);

        detailsVBox.getChildren().add(new Label("Flow ID: " + executionChoose.getUniqueId()));

        detailsVBox.getChildren().add(new Label("Execute at: " + executionChoose.getStartTimeOfExecution()));

        detailsVBox.getChildren().add(new Label("Duration: " + executionChoose.getTotalTime() + " milliseconds"));

        detailsVBox.getChildren().add(new Label("Result: " + executionChoose.getFlowExecutionStatus()));

        Text flowSummaryLine = new Text("Summary line: " + executionChoose.getFlowSummaryLine());
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
        for(StepUsageDeclaration step : executionChoose.getStepsInFlow()) {
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
        Map<IODefinitionData, Object> freeInputsMap = executionChoose.getFlowFreeInputs().getFromInputToObject();
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
        if(!executionChoose.getAllOutputProduceDuringFlow().keySet().isEmpty()) {
            for (IODefinitionData output : executionChoose.getAllOutputProduceDuringFlow().keySet()) {
                detailsVBox.getChildren().add(showDataDetails(executionChoose.getAllOutputProduceDuringFlow(), output, counter));
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
            for (int i = executionChoose.getContext().getStepsSummaryLine().size() - 1; i >= 0; i--) {
                if (executionChoose.getContext().getStepsSummaryLine().get(i).getStepName().equals(step.getStepName())) {
                    Text summary = new Text("Summary line: " + executionChoose.getContext().getStepsSummaryLine().get(i).getSummaryLine());
                    summary.setWrappingWidth(400);
                    detailsVBox.getChildren().add(summary);
                    break;
                }
            }

            // Duration
            Duration stepDuration = EngineController.getInstance().getStepDurationFromStepStatisticsManager(executionChoose, step);
            detailsVBox.getChildren().add(new Label("Start time: " + executionChoose.getFromStepToStart(step)));
            detailsVBox.getChildren().add(new Label("End time: " + executionChoose.getFromStepToEnd(step)));
            detailsVBox.getChildren().add(new Label("Duration: " + stepDuration.toMillis() + " ms"));


            // Get all execution inputs and outputs
            Map<StepUsageDeclaration, Map<IODefinitionData, Object>> allInputsAndOutputs = executionChoose.getFromStepToDataMap();
            Map<IODefinitionData, Object> stepMap = allInputsAndOutputs.get(step);

            // Step inputs
            Label stepInputs = new Label("Step Information");
            stepInputs.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            stepInputs.setStyle("-fx-text-fill: #28104e;");
            detailsVBox.getChildren().add(stepInputs);
            Integer counter = 1;
            for(IODefinitionData data : stepMap.keySet()) {
                if(data.getNecessity() != DataNecessity.NA) {
                    detailsVBox.getChildren().add(showDataDetails(stepMap, data, counter));
                    counter ++;
                }
            }

            // Step outputs
            Label stepOutputs = new Label("Step Information");
            stepOutputs.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            stepOutputs.setStyle("-fx-text-fill: #28104e;");
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
            for (StepLog stepLog : executionChoose.getContext().getFlowLogs()) {
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
}
