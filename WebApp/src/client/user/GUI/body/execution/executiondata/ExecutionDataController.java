package client.user.GUI.body.execution.executiondata;

import client.component.impl.*;
import client.user.GUI.body.UserBodyController;
import client.user.util.HttpUserUtil;
import com.google.gson.Gson;
import dto.flow.FlowExecutionDTO;
import dto.io.IODataValueDTO;
import dto.io.UserFreeInputsDTO;
import dto.step.StepExecutionDTO;
import dto.step.StepLogDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
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
import java.util.*;

public class ExecutionDataController {

    @FXML
    private RadioButton generalFlowDataRadioButton;
    @FXML
    private TitledPane stepsTitledPane;
    @FXML
    private VBox stepsTitledPaneContentVBox;
    @FXML
    private Button continuationButton;
    @FXML
    private VBox detailsVBox;
    @FXML
    private Accordion accordion;

    private UserBodyController userBodyController;
    private String runUUID;
    private String flowName;
    private String continueTo;
    private Timer timer;
    private SimpleBooleanProperty isExecutionEnded;
    private SimpleBooleanProperty isFlowContinuationChoose;


    public ExecutionDataController() {
        isExecutionEnded = new SimpleBooleanProperty(false);
        isFlowContinuationChoose = new SimpleBooleanProperty(false);
    }


    public void setBodyController(UserBodyController userBodyController) {
        this.userBodyController = userBodyController;
    }


    @FXML
    public void initialize() {
        accordion.disableProperty().bind(isExecutionEnded.not());
        continuationButton.disableProperty().bind(isExecutionEnded.not());
    }


    public void startScene(UserFreeInputsDTO userFreeInputsDTO) {
        isExecutionEnded.set(false);
        stepsTitledPaneContentVBox.getChildren().clear();
        detailsVBox.getChildren().clear();
        accordion.setExpandedPane(null);
        generalFlowDataRadioButton.setSelected(false);

        // Run the flow and show progress
        Gson gson = new Gson();
        String json = gson.toJson(userFreeInputsDTO);

        String finalUrl = HttpUrl
                .parse(Constants.RUN_FLOW)
                .newBuilder()
                .build()
                .toString();

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
                .url(finalUrl)
                .post(requestBody)
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
                    String uuid = gson.toJson(body, String.class);
                    Platform.runLater(() -> {
                        runUUID = uuid;

                        // Activate refresher and get process
                        ExecutionRefresher executionRefresher = new ExecutionRefresher(detailsVBox, runUUID);
                        timer = new Timer();
                        timer.schedule(executionRefresher, 0, 200);
                    });
                }
            }
        });
    }


    @FXML
    void executionTitledPaneSetOnMouseClick(MouseEvent event) {
        // Send request to get again the flow execution dto
        String finalUrl = HttpUrl
                .parse(Constants.GET_FLOW_EXECUTION_AFTER_EXECUTION)
                .newBuilder()
                .addQueryParameter(Constants.UUID, runUUID)
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
                    });
                }
            }
        });
    }


    public void showFlowExecutionDetails(FlowExecutionDTO flowExecutionDTO) {
        // Clear previous details
        detailsVBox.getChildren().clear();
        flowName = flowExecutionDTO.getFlowName();

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
        isFlowContinuationChoose.set(false);

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
    void continuationButtonSetOnAction(ActionEvent event) {
        String finalUrl = HttpUrl
                .parse(Constants.CONTINUATION)
                .newBuilder()
                .addQueryParameter(Constants.FLOW_NAME, flowName)
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
                    String jsonList = response.body().string();
                    Gson gson = new Gson();
                    String[] arr = gson.fromJson(jsonList, String[].class);
                    List<String> continuationNames = Arrays.asList(arr);
                    Platform.runLater(() -> {

                        // Clear previous data
                        detailsVBox.getChildren().clear();
                        isFlowContinuationChoose.set(false);

                        Label stepName = new Label("Possible Continuations");
                        stepName.setFont(Font.font("Arial", FontWeight.BOLD, 13));
                        detailsVBox.getChildren().add(stepName);
                        ToggleGroup toggleGroup = new ToggleGroup();

                        if(!continuationNames.isEmpty()) {
                            for(String continueName : continuationNames) {
                                RadioButton radioButton = new RadioButton(continueName);

                                radioButton.setToggleGroup(toggleGroup);
                                detailsVBox.getChildren().add(radioButton);
                            }

                            // Create start button
                            Button button = new Button("Continue to flow");
                            button.setOnAction(e -> {
                                // Send a http request to get user free inputs
                                String finalUrl = HttpUrl
                                        .parse(Constants.FREE_INPUTS_CONTINUATIONS)
                                        .newBuilder()
                                        .addQueryParameter(Constants.FLOW_NAME, continueTo)
                                        .addQueryParameter(Constants.UUID, runUUID)
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
                                            String inputs = response.body().string();
                                            UserFreeInputsDTO freeInputsDTOContinuations = gson.fromJson(inputs, UserFreeInputsDTO.class);
                                            userBodyController.executeFlowButtonPress(freeInputsDTOContinuations);
                                        }
                                    }
                                });
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
                                RadioButton selectedRadioButton = (RadioButton) newValue;
                                continueTo = selectedRadioButton.getText();
                                isFlowContinuationChoose.set(true);
                            });
                        }
                        else {
                            detailsVBox.getChildren().addAll(new Label(), new Label("No continuation has been defined for this flow !"));
                        }
                    });
                }
            }
        });
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    // Inner class. Refresher

    public class ExecutionRefresher extends TimerTask {

        private final VBox detailsVBox;
        private final String uuid;

        public ExecutionRefresher(VBox detailsVBox, String uuid) {
            this.detailsVBox = detailsVBox;
            this.uuid = uuid;
        }


        @Override
        public void run() {

            // Send http request to get flow execution dto
            String finalUrlUsers = HttpUrl
                    .parse(Constants.FLOW_EXECUTION)
                    .newBuilder()
                    .addQueryParameter(Constants.UUID, uuid)
                    .build()
                    .toString();

            Request request = new Request.Builder()
                    .url(finalUrlUsers)
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

                            if(!flowExecutionDTO.isProcessing()) {
                                timer.cancel();
                                isExecutionEnded.set(true);
                                flowName = flowExecutionDTO.getFlowName();
                            }

                            detailsVBox.getChildren().clear();

                            Label title = new Label("Execution Progress");
                            title.setStyle("-fx-text-fill: #28104e;");
                            title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                            detailsVBox.getChildren().add(title);


                            for (StepExecutionDTO step : flowExecutionDTO.getSteps()) {
                                try {
                                    String currentStepResult = step.getResult();
                                    HBox hBox = new HBox();
                                    hBox.setSpacing(3);
                                    hBox.getChildren().add(new Label("Step: " + step.getName() + "- "));
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
                }
            });
        }
    }

}
