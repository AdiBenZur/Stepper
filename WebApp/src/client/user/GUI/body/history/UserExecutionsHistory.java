package client.user.GUI.body.history;

import client.component.impl.FileListComponent;
import client.component.impl.MappingComponent;
import client.component.impl.RelationComponent;
import client.component.impl.StringListComponent;
import client.user.GUI.body.UserBodyController;
import client.user.util.HttpUserUtil;
import com.google.gson.Gson;
import dto.flow.ExecutionDetailsHeadlines;
import dto.flow.FlowExecutionDTO;
import dto.io.IODataValueDTO;
import dto.io.UserFreeInputsDTO;
import dto.step.StepExecutionDTO;
import dto.step.StepLogDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import servlets.util.Constants;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class UserExecutionsHistory {

    @FXML
    private TableView<ExecutionHistoryRowData> historyTableView;
    @FXML
    private TableColumn<ExecutionHistoryRowData, String> flowNameColumn;
    @FXML
    private TableColumn<ExecutionHistoryRowData, String> timeOfExecutionColumn;
    @FXML
    private TableColumn<ExecutionHistoryRowData, String> resultColumn;
    @FXML
    private TableColumn<ExecutionHistoryRowData, Button> seeDetailsColumn;
    @FXML
    private ChoiceBox<String> filterChoiceBox;
    @FXML
    private TitledPane executionDataTitledPane;
    @FXML
    private RadioButton generalFlowDataRadioButton;
    @FXML
    private VBox stepsTitledPaneContentVBox;
    @FXML
    private VBox detailsVBox;
    @FXML
    private Button rerunFlowButton;
    @FXML
    private SplitPane buttomSplitPane;

    private UserBodyController userBodyController;
    private SimpleBooleanProperty isFlowSeeDetailsChoose;
    private String currentUUID;


    public UserExecutionsHistory() {
        isFlowSeeDetailsChoose = new SimpleBooleanProperty(false);
    }

    public void setBodyController(UserBodyController userBodyController) {
        this.userBodyController = userBodyController;
    }

    @FXML
    public void initialize() {

        buttomSplitPane.disableProperty().bind(isFlowSeeDetailsChoose.not());

        flowNameColumn.setCellValueFactory(new PropertyValueFactory<ExecutionHistoryRowData, String>("name"));
        timeOfExecutionColumn.setCellValueFactory(new PropertyValueFactory<ExecutionHistoryRowData, String>("timeOfExecution"));
        resultColumn.setCellValueFactory(new PropertyValueFactory<ExecutionHistoryRowData, String>("result"));
        seeDetailsColumn.setCellValueFactory(new PropertyValueFactory<ExecutionHistoryRowData, Button>("seeDataButton"));

        filterChoiceBox.getItems().addAll("ALL", "Success", "Warning", "Failure");
        filterChoiceBox.setValue("ALL");

        // Add listener when filter changed
        filterChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Call the population methods to apply the filtering
            activate();
        });
    }



    public void activate() {
        // Send http request to get all user executions
        String finalUrl = HttpUrl
                .parse(Constants.USER_EXECUTIONS)
                .newBuilder()
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        HttpUserUtil.runAsync(request, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    String listString = response.body().string();
                    Gson gson = new Gson();
                    ExecutionDetailsHeadlines[] arr = gson.fromJson(listString, ExecutionDetailsHeadlines[].class);
                    List<ExecutionDetailsHeadlines> listOfExecutions = Arrays.asList(arr);
                    Platform.runLater(() -> {
                        isFlowSeeDetailsChoose.set(false);
                        generalFlowDataRadioButton.setSelected(false);
                        detailsVBox.getChildren().clear();
                        stepsTitledPaneContentVBox.getChildren().clear();
                        fillTableView(listOfExecutions);
                    });
                }
            }
        });
    }


    public void fillTableView(List<ExecutionDetailsHeadlines> executions) {
        rerunFlowButton.setVisible(false);
        ObservableList<ExecutionHistoryRowData> data = FXCollections.observableArrayList();
        String filterOption = filterChoiceBox.getValue();

        for(ExecutionDetailsHeadlines headlines : executions) {
            String name = headlines.getFlowName();
            String time = headlines.getStartTimeOfExecutions();
            String result = headlines.getResult();
            Button button = new Button("Execution Details");

            button.setOnAction(e -> {
                isFlowSeeDetailsChoose.set(true);
                rerunFlowButton.setVisible(true);
                stepsTitledPaneContentVBox.getChildren().clear();
                currentUUID = headlines.getUuid();
                currentUUID = "\"" + currentUUID + "\"";
                showExecutionDetails();
            });

            if(filterOption.equals("ALL") || headlines.getResult().equals(filterOption)) {
                ExecutionHistoryRowData historyRowData = new ExecutionHistoryRowData(name, time, result, null, button);
                data.add(historyRowData);
            }
        }
        historyTableView.setItems(data);
    }

    public void showExecutionDetails() {
        // Send http request to get execution

        String finalUrl = HttpUrl
                .parse(Constants.GET_FLOW_EXECUTION_AFTER_EXECUTION)
                .newBuilder()
                .addQueryParameter(Constants.UUID, currentUUID)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        HttpUserUtil.runAsync(request, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    Gson gson = new Gson();
                    String body = response.body().string();
                    FlowExecutionDTO flowExecutionDTO = gson.fromJson(body, FlowExecutionDTO.class);
                    Platform.runLater(() -> {
                        ToggleGroup toggleGroup = new ToggleGroup();

                        generalFlowDataRadioButton.setToggleGroup(toggleGroup);
                        generalFlowDataRadioButton.setOnAction(event -> {
                            showFlowExecutionDetails(flowExecutionDTO);
                        });

                        // Add steps
                        for(int i = 0; i < flowExecutionDTO.getStepNames().size(); i ++) {

                            String name = flowExecutionDTO.getStepNames().get(i);

                            // Find the match stepExecutionDTO
                            StepExecutionDTO correlateStep = null;
                            for(StepExecutionDTO step : flowExecutionDTO.getSteps()) {
                                if(step.getName().equals(name)) {
                                    correlateStep = step;
                                    break;
                                }
                            }

                            RadioButton radioButton = new RadioButton("Step " + (i + 1) + ": " + name);
                            radioButton.setToggleGroup(toggleGroup);

                            StepExecutionDTO finalCorrelateStep = correlateStep;

                            radioButton.setOnAction(event -> {
                                showStepExecutionDetails(finalCorrelateStep, flowExecutionDTO);
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
                        showFlowExecutionDetails(flowExecutionDTO);
                    });
                }
            }
        });
    }

    public void showFlowExecutionDetails(FlowExecutionDTO flowExecutionDTO) {
        // Clear previous details
        detailsVBox.getChildren().clear();

        Label flowName = new Label("Flow name: " + flowExecutionDTO.getFlowName());
        flowName.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        detailsVBox.getChildren().add(flowName);

        detailsVBox.getChildren().add(new Label("Flow ID: " + flowExecutionDTO.getUniqueId()));

        detailsVBox.getChildren().add(new Label("Execute at: " + flowExecutionDTO.getStartTimeOfExecution()));

        detailsVBox.getChildren().add(new Label("Duration: " + flowExecutionDTO.getDuration() + " milliseconds"));

        detailsVBox.getChildren().add(new Label("Result: " + flowExecutionDTO.getExecutionResult()));


        Text flowSummaryLine = new Text("Summary line: " + flowExecutionDTO.getFlowSummaryLine());
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
        boolean isExist;
        for(String stepName : flowExecutionDTO.getStepNames()) {
            isExist = false;
            for(StepExecutionDTO step : flowExecutionDTO.getSteps()) {
                if(step.getName().equals(stepName)) {
                    isExist = true;
                    String stepResult = step.getResult();
                    HBox hBox = new HBox();
                    hBox.setSpacing(3);
                    hBox.getChildren().add(new Label("Name: " + step.getName() + "- "));
                    Label result = new Label(stepResult);
                    // Set colors
                    if (stepResult.equals("Success"))
                        result.setStyle("-fx-text-fill: #1d8348;");
                    if (stepResult.equals("Failure"))
                        result.setStyle("-fx-text-fill: #ff0000;");
                    if (stepResult.equals("Warning"))
                        result.setStyle("-fx-text-fill: #ff8c00;");

                    hBox.getChildren().add(result);
                    vBox.getChildren().add(hBox);
                    break;
                }
            }
            if(!isExist) {
                vBox.getChildren().add(new Label("Name: " + stepName + "- Does not executed"));
            }
        }

        detailsVBox.getChildren().add(vBox);
        detailsVBox.getChildren().add(new Label());

        // Free inputs
        Label freeInputs = new Label("Free Inputs Details");
        freeInputs.setStyle("-fx-text-fill: #28104e;");
        freeInputs.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        detailsVBox.getChildren().add(freeInputs);

        detailsVBox.getChildren().add(new Label("Mandatory Inputs: "));
        Integer counter = 1;
        for(IODataValueDTO input : flowExecutionDTO.getFreeInputsValues()) {
            if(input.getNecessity().equals(Constants.MANDATORY)) {
                detailsVBox.getChildren().add(showDataDetails(input, counter));
                counter ++;
            }
        }

        counter = 1;
        if(isThereOptionalInputs(flowExecutionDTO.getFreeInputsValues())) {
            detailsVBox.getChildren().add(new Label("Optionals Inputs: "));
            for (IODataValueDTO input : flowExecutionDTO.getFreeInputsValues()) {
                if (input.getNecessity().equals(Constants.OPTIONAL)) {
                    detailsVBox.getChildren().add(showDataDetails(input, counter));
                    counter ++;
                }
            }
        }
        else
            detailsVBox.getChildren().add(new Label("No optional inputs inserted."));

        // All outputs produce during flow
        Label allOutputs = new Label("Outputs produce during the flow");
        allOutputs.setStyle("-fx-text-fill: #28104e;");
        allOutputs.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        detailsVBox.getChildren().add(allOutputs);
        counter = 1;
        if(!flowExecutionDTO.getAllOutputProduceDuringFlow().isEmpty()) {
            for (IODataValueDTO output : flowExecutionDTO.getAllOutputProduceDuringFlow()) {
                detailsVBox.getChildren().add(showDataDetails(output, counter));
                counter++;
            }
        }
        else
            detailsVBox.getChildren().add(new Label("No outputs produce during flow!"));
    }


    public Boolean isThereOptionalInputs(List<IODataValueDTO> inputs) {
        for(IODataValueDTO input : inputs) {
            if(input.getNecessity().equals(Constants.OPTIONAL))
                return true;
        }
        return false;
    }


    public VBox showDataDetails(IODataValueDTO data, Integer counter) {
        VBox dataVbox = new VBox();
        dataVbox.setSpacing(3);

        dataVbox.getChildren().add(new Label(counter + ". Name: " + data.getName()));
        dataVbox.getChildren().add(new Label("   Type: " + data.getDataDefinition()));
        dataVbox.getChildren().add(new Label("   User string: " + data.getUserString()));
        HBox hBox = new HBox();
        hBox.setSpacing(5);
        hBox.getChildren().add(new Label("   Value: "));
        if(data.getDataDefinition().equals("RelationData")) {
            Button button = new Button("See relation data");
            button.setOnAction(e -> {
                Stage popupStage = new Stage();
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.setTitle("Table View");
                Scene popupScene = new Scene(showComponent(data));
                popupStage.setScene(popupScene);
                popupStage.show();
            });
            hBox.getChildren().add(button);
        }
        else {
            hBox.getChildren().add(showComponent(data));
        }

        dataVbox.getChildren().add(hBox);

        if(!data.getNecessity().equals(Constants.OUTPUT))
            dataVbox.getChildren().add(new Label("   Necessity: " + data.getNecessity().toString()));

        return dataVbox;
    }


    public Parent showComponent(IODataValueDTO data) {

        if(data.getDataDefinition().equals("FileList")) {
            FileListComponent fileListComponent = new FileListComponent(data);
            return fileListComponent.showComponent();
        }

        if(data.getDataDefinition().equals("StringList")) {
            StringListComponent stringListComponent = new StringListComponent(data);
            return stringListComponent.showComponent();
        }

        if(data.getDataDefinition().equals("MappingData")) {
            MappingComponent mappingComponent = new MappingComponent(data);
            return mappingComponent.showComponent();
        }

        if(data.getDataDefinition().equals("RelationData")) {
            RelationComponent relationComponent = new RelationComponent(data);
            return relationComponent.showComponent();
        }

        if(data.getDataDefinition().equals("Integer")) {
            // Casting to int
            Double d = (Double)data.getValue();
            int toInt = (int) Math.round(d);
            return new Label("" + toInt);
        }

        if(data.getDataDefinition().equals("JsonObject")) {
            VBox vBox = new VBox();
            Text text = new Text(data.getValue().toString());
            text.setWrappingWidth(400);
            vBox.getChildren().add(text);
            return vBox;
        }

        return new Label(data.getValue().toString());
    }


    public void showStepExecutionDetails(StepExecutionDTO step, FlowExecutionDTO flowExecutionDTO) {

        // Clear previous details
        detailsVBox.getChildren().clear();

        Label stepName = new Label("Step Information");
        stepName.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        detailsVBox.getChildren().add(stepName);

        if(step == null) {
            detailsVBox.getChildren().add(new Label("The flow failed before the step was run! "));
        }
        else {
            // Add name
            if(step.getName().equals(step.getOriginalName()))
                detailsVBox.getChildren().add(new Label("Name: " + step.getName()));
            else {
                detailsVBox.getChildren().add(new Label("Name: " + step.getName()));
                detailsVBox.getChildren().add(new Label("Original name: " + step.getOriginalName()));
            }

            // Result
            String currentStepResult = step.getResult();
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
            Text summary = new Text("Summary line: " + step.getSummaryLine());
            summary.setWrappingWidth(400);
            detailsVBox.getChildren().add(summary);

            // Duration
            detailsVBox.getChildren().add(new Label("Start time: " + step.getStartTime()));
            detailsVBox.getChildren().add(new Label("End time: " + step.getEndTime()));
            detailsVBox.getChildren().add(new Label("Duration: " + step.getDuration() + " ms"));

            // Step inputs
            detailsVBox.getChildren().add(new Label());
            Label stepInputs = new Label("Step Inputs");
            stepInputs.setStyle("-fx-text-fill: #28104e;");
            stepInputs.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            detailsVBox.getChildren().add(stepInputs);
            Integer counter = 1;
            for(IODataValueDTO data : step.getStepDataValues()) {
                if(!data.getNecessity().equals("NA")) {
                    detailsVBox.getChildren().add(showDataDetails(data, counter));
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
            for(IODataValueDTO data : step.getStepDataValues()) {
                if(data.getNecessity().equals(Constants.OUTPUT)) {
                    detailsVBox.getChildren().add(showDataDetails(data, counter));
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
            for(StepLogDTO stepLog : flowExecutionDTO.getStepLogs()) {
                if(stepLog.getStepName().equals(step.getName())) {
                    Text log = new Text(stepLog.getLog());
                    log.setWrappingWidth(400);
                    stepLogsVbox.getChildren().add(log);
                }
            }
            detailsVBox.getChildren().add(stepLogsVbox);
        }
    }





    @FXML
    void rerunFlowButtonSetOnAction(ActionEvent event) {
        // Get user free inputs of this run by uuid

        String finalUrl = HttpUrl
                .parse(Constants.FREE_INPUTS_RERUN)
                .newBuilder()
                .addQueryParameter(Constants.UUID, currentUUID)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        HttpUserUtil.runAsync(request, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    String inputsJson = response.body().string();
                    Gson gson = new Gson();
                    UserFreeInputsDTO userFreeInputsDTO = gson.fromJson(inputsJson, UserFreeInputsDTO.class);

                    // Move to scene
                    Platform.runLater(() -> {
                        userBodyController.isRerunButtonPress(userFreeInputsDTO);
                    });
                }
            }
        });
    }
}
