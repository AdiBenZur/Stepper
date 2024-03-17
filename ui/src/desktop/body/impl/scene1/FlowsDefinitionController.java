package desktop.body.impl.scene1;

import desktop.application.EngineController;
import desktop.body.BodyController;
import desktop.body.api.SceneBody;
import exception.data.TypeDontMatchException;
import flow.definition.api.FlowDefinition;
import flow.definition.api.StepUsageDeclaration;
import io.api.DataNecessity;
import io.api.IODefinitionData;
import io.impl.UserFreeInputs;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import manager.system.operation.FlowInformation;

import java.util.List;
import java.util.Map;


public class FlowsDefinitionController implements SceneBody {

    @FXML
    private VBox availableFlowsVbox;
    @FXML
    private VBox flowDefinitionVBox;
    @FXML
    private Button executeFlowButton;



    private BodyController bodyController;
    private SimpleBooleanProperty isButtonSelectedProperty;
    private FlowDefinition flowChoose;



    public FlowsDefinitionController() {
        isButtonSelectedProperty = new SimpleBooleanProperty(false);
    }


    @FXML
    private void initialize() {
        flowDefinitionVBox.disableProperty().bind(isButtonSelectedProperty.not());
    }

    public void setBodyController(BodyController bodyController) {
        this.bodyController = bodyController;
    }


    @Override
    public void fillScene() {
        executeFlowButton.setVisible(false);

        Integer serialNumber = 0;
        // Add buttons
        availableFlowsVbox.getChildren().clear();
        Label availableFlowsVboxTitle = new Label("Available Flows:");
        availableFlowsVboxTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        availableFlowsVboxTitle.setStyle("-fx-text-fill: #28104e;");
        availableFlowsVbox.getChildren().add(availableFlowsVboxTitle);
        availableFlowsVbox.getChildren().add(new Label());


        ToggleGroup toggleGroup = new ToggleGroup();
        for(FlowDefinition flowDefinition : EngineController.getInstance().getStepper().getAllFlows()) {
            RadioButton radioButton = new RadioButton(flowDefinition.getName());
            radioButton.setToggleGroup(toggleGroup);

            final Integer finalSerial = serialNumber; // for lambda

            // For each button, add css class
            radioButton.getStyleClass().add("flow-definition-radio-button");

            // For each button, set action
            radioButton.setOnAction(event -> {
                String flowChooseName = radioButton.getText();
                flowChoose = EngineController.getInstance().getStepper().getFlowDefinitionByName(flowChooseName);
                isButtonSelectedProperty.set(true);
                executeFlowButton.setVisible(true);

                FlowInformation flowInformation = EngineController.getInstance().showFlowInformation(EngineController.getInstance().getFlowNamesList().get(finalSerial));
                showFlowInformation(flowInformation);
            });

            serialNumber ++;
            availableFlowsVbox.getChildren().add(radioButton);
        }

    }



    public void showFlowInformation(FlowInformation flowInformation) {
        // Clear information
        flowDefinitionVBox.getChildren().clear();


        // Add flow details to VBox

        // Add flow name
        Label name = new Label("Flow Name: " + flowInformation.getName());
        name.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        flowDefinitionVBox.getChildren().add(name);

        // Add description
        Text text = new Text("Description: " + flowInformation.getFlowDescription());
        text.setWrappingWidth(380);
        text.setStyle("-fx-control-inner-background: #cccccc;");
        flowDefinitionVBox.getChildren().add(text);

        // Add formal outputs
        Label formalOutputs = new Label();
        StringBuilder formalOut = new StringBuilder();
        formalOut.append("Flow Formal Outputs: ");
        for(IODefinitionData formalOutput : flowInformation.getFormalOutputs())
            formalOut.append(formalOutput.getName()).append(",");
        formalOut.deleteCharAt(formalOut.length() - 1);
        formalOutputs.setText(formalOut.toString());
        flowDefinitionVBox.getChildren().add(formalOutputs);

        // Add read only
        flowDefinitionVBox.getChildren().add(new Label("Read only: " + (flowInformation.getRadOnly() ? "Yes" : "No")));

        // Separator
        flowDefinitionVBox.getChildren().add(new Label(""));

        // Add steps
        Label stepsTitle = new Label("Steps");
        stepsTitle.setStyle("-fx-text-fill: #28104e;");
        stepsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        flowDefinitionVBox.getChildren().add(stepsTitle);


        for(StepUsageDeclaration step : flowInformation.getSteps()) {

            VBox stepVbox = new VBox();
            stepVbox.setSpacing(3);
            Label stepName = new Label();
            if(!step.getStepName().equals(step.getStepDefinition().getName()))
                stepName.setText( "Name: " + step.getStepDefinition().getName() + ", " + step.getStepName());
            else
                stepName.setText("Name: " + step.getStepName());

            stepVbox.getChildren().add(stepName);
            stepVbox.getChildren().add(new Label("Read only: " + (step.getStepDefinition().isReadOnly() ? "Yes" : "No")));

            TitledPane stepTitledPane = new TitledPane();
            stepTitledPane.setText("Step Details");
            VBox content = new VBox();
            content.setSpacing(10);
            content = addStepDetails(step, flowChoose);
            stepTitledPane.setContent(content);
            stepTitledPane.setExpanded(false);

            stepVbox.getChildren().add(stepTitledPane);

            flowDefinitionVBox.getChildren().add(stepVbox);
        }

        // Separator
        flowDefinitionVBox.getChildren().add(new Label(""));

        // Add free inputs
        Label freeInputs = new Label("Flow Free Inputs ");
        freeInputs.setStyle("-fx-text-fill: #28104e;");
        freeInputs.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        flowDefinitionVBox.getChildren().add(freeInputs);

        Integer counter = 1;
        for(IODefinitionData freeInput : flowInformation.getFreeInputs()) {
            VBox inputs = new VBox();
            inputs.setSpacing(3);
            Label inputName = new Label(counter + ". Name: " + freeInput.getName());
            Label inputType = new Label("   Type: " + freeInput.getDataDefinition().getName());

            List<StepUsageDeclaration> linkedSteps = flowInformation.getLinkedStepsByInputKey(freeInput);
            Label inputLikedSteps = new Label();
            StringBuilder steps = new StringBuilder();
            steps.append("   Linked Steps: ");
            for(StepUsageDeclaration step : linkedSteps)
                steps.append(step.getStepName()).append(",");

            steps.deleteCharAt(steps.length() - 1);
            inputLikedSteps.setText(steps.toString());

            Label inputNecessity = new Label("   Mandatory/ Optional: " + freeInput.getNecessity().toString());


            inputs.getChildren().add(inputName);
            inputs.getChildren().add(inputType);
            inputs.getChildren().add(inputLikedSteps);
            inputs.getChildren().add(inputNecessity);

            flowDefinitionVBox.getChildren().add(inputs);
            counter ++;
        }

        // Separator
        flowDefinitionVBox.getChildren().add(new Label(""));

        // Add all output produce during flow
        Label outputsTitle = new Label("Flow Outputs ");
        outputsTitle.setStyle("-fx-text-fill: #28104e;");
        outputsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        flowDefinitionVBox.getChildren().add(outputsTitle);

        counter = 1;
        for(IODefinitionData output : flowInformation.getAllOutputs()) {
            VBox outputs = new VBox();
            outputs.setSpacing(3);
            Label outputName = new Label(counter + ". Name: " + output.getName());
            Label outputType = new Label("   Name: " + output.getDataDefinition().getName());
            Label outputProduceStep = new Label("   Produce Step: " + flowInformation.getStepProduceByKey(output).getStepName());

            outputs.getChildren().add(outputName);
            outputs.getChildren().add(outputType);
            outputs.getChildren().add(outputProduceStep);

            flowDefinitionVBox.getChildren().add(outputs);
            counter ++;
        }

    }

    @FXML
    void executeFlowButtonSetOnAction(ActionEvent event) {
        UserFreeInputs userFreeInputs = EngineController.getInstance().getFlowUserInputs(flowChoose);

        try {
            userFreeInputs.insertInitValues();
        } catch (TypeDontMatchException e) {

        }
        bodyController.flowExecutionButtonPress(flowChoose, userFreeInputs);
    }


    public VBox addStepDetails(StepUsageDeclaration step, FlowDefinition flow) {
        Map<StepUsageDeclaration, Map<IODefinitionData, IODefinitionData>> mapping = flow.getMappings();
        Map<IODefinitionData, IODefinitionData> stepMapping = mapping.get(step);

        VBox dataDetailsVBox = new VBox();
        dataDetailsVBox.setSpacing(8);

        Label inputLabel = new Label("Inputs:");
        inputLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        dataDetailsVBox.getChildren().add(inputLabel);

        // Inputs details
        // Run over step mapping values
        Integer counter = 1;
        for(IODefinitionData data : stepMapping.values()) {
            if(data.getNecessity() != DataNecessity.NA) {

                // It's an input
                VBox inputsVbox = new VBox();
                inputsVbox.setSpacing(3);

                inputsVbox.getChildren().add(new Label(counter + ". Name: " + data.getName()));
                inputsVbox.getChildren().add(new Label("   Mandatory/ Optional: " + data.getNecessity()));

                IODefinitionData result = flow.isInputConnectToOutput(step, data);

                if(result != null) {
                    // Found connected output
                    Text connect = new Text("   Connect to output name: " + result.getName() + ", that coming from step name: " + flow.fromOutputToStepProduce(result).getStepName());
                    connect.setWrappingWidth(380);
                    inputsVbox.getChildren().add(connect);
                }
                else {
                    inputsVbox.getChildren().add(new Label("   Connect to output: false" ));
                }
                dataDetailsVBox.getChildren().add(inputsVbox);
                counter ++;
            }


        }


        Label outputLabel = new Label("Outputs:");
        outputLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        dataDetailsVBox.getChildren().add(outputLabel);

        // Outputs details
        Integer counter2 = 1;
        for(IODefinitionData data : stepMapping.values()) {


            if(data.getNecessity() == DataNecessity.NA) {
                // It's an output
                VBox outputVbox = new VBox();
                outputVbox.setSpacing(3);

                outputVbox.getChildren().add(new Label(counter2 + ". Name: " + data.getName()));

                IODefinitionData result = flow.isOutputConnectToInput(step, data);

                if(result != null) {

                    // Found connected input
                    Text connected = new Text("   Connect to input name: " + result.getName() + ", that coming from step name: " + flow.fromOutputToStepProduce(result).getStepName());
                    connected.setWrappingWidth(380);
                    outputVbox.getChildren().add(connected);
                }
                else {
                    outputVbox.getChildren().add(new Label("   Connect to input: false" ));
                }
                dataDetailsVBox.getChildren().add(outputVbox);
                counter2 ++;
            }
        }

        return dataDetailsVBox;
    }
}
