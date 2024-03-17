package client.user.GUI.body.definition;

import client.user.GUI.body.UserBodyController;
import client.user.util.HttpUserUtil;
import com.google.gson.Gson;
import dto.flow.FlowInformationDTO;
import dto.io.IODefinitionDataDTO;
import dto.io.IOInStepMappingDTO;
import dto.io.UserFreeInputsDTO;
import dto.step.StepUsageDeclarationDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import servlets.util.Constants;

import java.io.IOException;
import java.util.*;

public class FlowDefinitionController {

    public static final int REFRESH_RATE = 1000;

    @FXML
    private VBox availableFlowsVBox;
    @FXML
    private VBox flowDetailsVBox;
    @FXML
    private Button executeFlowButton;

    private UserBodyController userBodyController;
    private Timer timer;
    private TimerTask listRefresher;
    private String selectedFlowName;
    private SimpleBooleanProperty isFlowSelected;
    private ToggleGroup userFlowsToggleGroup;
    private List<String> availableFlows;
    private List<RadioButton> availableFlowsRadioButtons;
    private boolean isLabelInside;
    private Label noAllowedFlowsLabel;

    public FlowDefinitionController() {
        isFlowSelected = new SimpleBooleanProperty(false);
        userFlowsToggleGroup= new ToggleGroup();
        availableFlows = new ArrayList<>();
        availableFlowsRadioButtons = new ArrayList<>();
        noAllowedFlowsLabel = new Label("There are no flows allowed for user yet.");
    }

    public void setBodyController(UserBodyController userBodyController) {
        this.userBodyController = userBodyController;
    }

    @FXML
    public void initialize() {
        flowDetailsVBox.disableProperty().bind(isFlowSelected.not());
        executeFlowButton.setVisible(false);
    }

    public void startScene() {
        availableFlowsVBox.getChildren().clear();
        availableFlows.clear();
        userFlowsToggleGroup.getToggles().clear();
        isFlowSelected.set(false);
        executeFlowButton.setVisible(false);
        isLabelInside = false;
        flowDetailsVBox.getChildren().clear();

        startListRefresher();
    }

    public void updateUserAllowedFlowsList(List<String> flowsList) {
        // Update VBox
        Platform.runLater(() -> {
            activate(flowsList);
        });
    }

    public void startListRefresher() {
        listRefresher = new UserAllowedFlowsRefresher(
                this::updateUserAllowedFlowsList);
        timer = new Timer();
        timer.schedule(listRefresher, 0, REFRESH_RATE);
    }


    public void activate(List<String> allowedFlowsList) {

        // Remove all the flows that not allowed anymore
        Iterator<String> iterator = availableFlows.iterator();
        while(iterator.hasNext()) {
            String flowName = iterator.next();
            if(!allowedFlowsList.contains(flowName)) {

                // Need to delete the role
                // Find the correlate radio button
                for(RadioButton radioButton : availableFlowsRadioButtons) {
                    if(radioButton.getText().equals(flowName)) {
                        availableFlowsVBox.getChildren().remove(radioButton);
                        iterator.remove();
                    }
                }
            }
        }

        if(allowedFlowsList.isEmpty()) {
           if(!isLabelInside) {
               availableFlowsVBox.getChildren().add(noAllowedFlowsLabel);
               isLabelInside = true;
           }
        }
        else {
            for(String flowName : allowedFlowsList) {
                if(!availableFlows.contains(flowName)) {
                    // There is at least one flow
                    if(isLabelInside) {
                        isLabelInside = false;
                        availableFlowsVBox.getChildren().clear();
                    }

                    // Need to add flow to the scene
                    RadioButton radioButton = new RadioButton(flowName);
                    radioButton.setToggleGroup(userFlowsToggleGroup);
                    availableFlowsRadioButtons.add(radioButton);
                    availableFlows.add(flowName);

                    // For each button, set action
                    radioButton.setOnAction(event -> {
                        String flowChooseName = radioButton.getText();
                        isFlowSelected.set(true);
                        executeFlowButton.setVisible(true);
                        showFlowInformation(flowChooseName);
                    });
                    availableFlowsVBox.getChildren().add(radioButton);
                }
            }
        }
    }


    public void showFlowInformation(String flowName) {

        String finalUrl = HttpUrl
                .parse(Constants.FLOW_INFORMATION)
                .newBuilder()
                .addQueryParameter(Constants.FLOW_INFORMATION_NAME, flowName)
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
                    String flowInformationString = response.body().string();
                    FlowInformationDTO flowInformationDTO = gson.fromJson(flowInformationString, FlowInformationDTO.class);
                    Platform.runLater(() -> {
                        printDetails(flowInformationDTO);
                        selectedFlowName = flowName;
                    });
                }
            }
        });
    }


    public void printDetails(FlowInformationDTO flowInformationDTO) {
        // Clear information
        flowDetailsVBox.getChildren().clear();

        // Add flow details to VBox

        // Add flow name
        Label name = new Label("Flow Name: " + flowInformationDTO.getName());
        name.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        flowDetailsVBox.getChildren().add(name);

        // Add description
        Text text = new Text("Description: " + flowInformationDTO.getDescription());
        text.setWrappingWidth(380);
        text.setStyle("-fx-control-inner-background: #cccccc;");
        flowDetailsVBox.getChildren().add(text);

        // Add formal outputs
        Label formalOutputs = new Label();
        StringBuilder formalOut = new StringBuilder();
        formalOut.append("Flow Formal Outputs: ");
        for(String formalOutput : flowInformationDTO.getFormalOutputs())
            formalOut.append(formalOutput).append(",");
        formalOut.deleteCharAt(formalOut.length() - 1);
        formalOutputs.setText(formalOut.toString());
        flowDetailsVBox.getChildren().add(formalOutputs);

        // Add read only
        flowDetailsVBox.getChildren().add(new Label("Read only: " + (flowInformationDTO.isReadOnly() ? "Yes" : "No")));

        // Separator
        flowDetailsVBox.getChildren().add(new Label(""));

        // Add steps
        Label stepsTitle = new Label("Steps");
        stepsTitle.setStyle("-fx-text-fill: #28104e;");
        stepsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        flowDetailsVBox.getChildren().add(stepsTitle);

        for(StepUsageDeclarationDTO step : flowInformationDTO.getSteps()) {

            VBox stepVbox = new VBox();
            stepVbox.setSpacing(3);
            Label stepName = new Label();
            if(!step.getFinalName().equals(step.getStepDefinitionDTO().getStepName()))
                stepName.setText( "Name: " + step.getStepDefinitionDTO().getStepName() + ", " + step.getFinalName());
            else
                stepName.setText("Name: " + step.getFinalName());

            stepVbox.getChildren().add(stepName);
            stepVbox.getChildren().add(new Label("Read only: " + (step.getStepDefinitionDTO().isReadOnly() ? "Yes" : "No")));

            TitledPane stepTitledPane = new TitledPane();
            stepTitledPane.setText("Step Details");
            VBox content = new VBox();
            content.setSpacing(10);
            content = addStepDetails(step);
            stepTitledPane.setContent(content);
            stepTitledPane.setExpanded(false);

            stepVbox.getChildren().add(stepTitledPane);

            flowDetailsVBox.getChildren().add(stepVbox);
        }

        // Separator
        flowDetailsVBox.getChildren().add(new Label(""));

        // Add free inputs
        Label freeInputs = new Label("Flow Free Inputs ");
        freeInputs.setStyle("-fx-text-fill: #28104e;");
        freeInputs.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        flowDetailsVBox.getChildren().add(freeInputs);

        Integer counter = 1;
        for(IODefinitionDataDTO freeInput : flowInformationDTO.getFreeInputs()) {
            VBox inputs = new VBox();
            inputs.setSpacing(3);
            Label inputName = new Label(counter + ". Name: " + freeInput.getName());
            Label inputType = new Label("   Type: " + freeInput.getType());

            List<String> linkedSteps = flowInformationDTO.getLinkedStepsByInputKey(freeInput);
            Label inputLikedSteps = new Label();
            StringBuilder steps = new StringBuilder();
            steps.append("   Linked Steps: ");
            for(String stepName : linkedSteps)
                steps.append(stepName).append(",");

            steps.deleteCharAt(steps.length() - 1);
            inputLikedSteps.setText(steps.toString());

            Label inputNecessity = new Label("   Necessity: " + freeInput.getNecessity().toString());


            inputs.getChildren().addAll(inputName, inputType, inputLikedSteps, inputNecessity);

            flowDetailsVBox.getChildren().add(inputs);
            counter ++;
        }

        // Separator
        flowDetailsVBox.getChildren().add(new Label(""));

        // Add all output produce during flow
        Label outputsTitle = new Label("Flow Outputs ");
        outputsTitle.setStyle("-fx-text-fill: #28104e;");
        outputsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        flowDetailsVBox.getChildren().add(outputsTitle);

        counter = 1;
        for(IODefinitionDataDTO output : flowInformationDTO.getAllOutputs()) {
            VBox outputs = new VBox();
            outputs.setSpacing(3);
            Label outputName = new Label(counter + ". Name: " + output.getName());
            Label outputType = new Label("   Name: " + output.getType());
            Label outputProduceStep = new Label("   Produce Step: " + flowInformationDTO.getStepProduceByKey(output));

            outputs.getChildren().add(outputName);
            outputs.getChildren().add(outputType);
            outputs.getChildren().add(outputProduceStep);

            flowDetailsVBox.getChildren().add(outputs);
            counter ++;
        }
    }


    public VBox addStepDetails(StepUsageDeclarationDTO step) {
        VBox dataDetailsVBox = new VBox();
        dataDetailsVBox.setSpacing(8);

        Label inputLabel = new Label("Inputs:");
        inputLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        dataDetailsVBox.getChildren().add(inputLabel);

        // Inputs details
        Integer counter = 1;
        for(IOInStepMappingDTO input : step.getInputs()) {
            VBox inputsVbox = new VBox();
            inputsVbox.setSpacing(3);

            inputsVbox.getChildren().add(new Label(counter + ". Name: " + input.getName()));
            inputsVbox.getChildren().add(new Label("   Mandatory/ Optional: " + input.getNecessity()));

            if(input.getIsConnectedToOther() != null) {
                Text connect = new Text("   Connect to output name: " + input.getIsConnectedToOther() + ", that coming from step name: " + input.getStepNameOfConnectedData());
                connect.setWrappingWidth(380);
                inputsVbox.getChildren().add(connect);
            }
            else
                inputsVbox.getChildren().add(new Label("   Connect to output: false" ));

            dataDetailsVBox.getChildren().add(inputsVbox);
            counter ++;
        }

        Label outputLabel = new Label("Outputs:");
        outputLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        dataDetailsVBox.getChildren().add(outputLabel);

        // Outputs details
        Integer counter2 = 1;
        for(IOInStepMappingDTO output : step.getOutputs()) {
            VBox outputVbox = new VBox();
            outputVbox.setSpacing(3);

            outputVbox.getChildren().add(new Label(counter2 + ". Name: " + output.getName()));
            if(output.getIsConnectedToOther() != null) {
                // Found connected input
                Text connected = new Text("   Connect to input name: " + output.getIsConnectedToOther() + ", that coming from step name: " + output.getStepNameOfConnectedData());
                connected.setWrappingWidth(380);
                outputVbox.getChildren().add(connected);
            }
            else
                outputVbox.getChildren().add(new Label("   Connect to input: false" ));

            dataDetailsVBox.getChildren().add(outputVbox);
            counter2 ++;
        }

        return dataDetailsVBox;
    }



    @FXML
    void executeFlowButtonSetOnAction(ActionEvent event) {

        // Get free inputs
        String finalUrl = HttpUrl
                .parse(Constants.FREE_INPUTS)
                .newBuilder()
                .addQueryParameter(Constants.FLOW_NAME_TO_EXECUTE, selectedFlowName)
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
                    String userInputJson = response.body().string();
                    UserFreeInputsDTO userFreeInputsDTO = gson.fromJson(userInputJson, UserFreeInputsDTO.class);
                    Platform.runLater(() -> {
                        userBodyController.executeFlowButtonPress(userFreeInputsDTO);
                    });
                }
                else {
                    // The user not allowed to run the flow anymore. print message and clear vbox
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("The current user does not have permission to run the flow.");
                        alert.showAndWait();

                        flowDetailsVBox.getChildren().clear();
                    });
                }
            }
        });
    }

}
