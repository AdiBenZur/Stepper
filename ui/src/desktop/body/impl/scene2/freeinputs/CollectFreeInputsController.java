package desktop.body.impl.scene2.freeinputs;

import datadefinition.impl.enumerator.type.ZipperEnumerator;
import desktop.body.api.SceneBody;
import desktop.body.impl.scene2.FlowExecutionController;
import exception.data.TypeDontMatchException;
import flow.definition.api.FlowDefinition;
import flow.execution.FlowExecution;
import io.api.DataNecessity;
import io.api.IODefinitionData;
import io.impl.UserFreeInputs;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

public class CollectFreeInputsController implements SceneBody {

    @FXML
    private VBox freeInputsVbox;
    @FXML
    private Button startExecuteButton;

    private FlowDefinition flow;
    private FlowExecution flowExecutionToRerun = null;
    private final List<TextField> mandatoryInputsTextField;
    private final SimpleBooleanProperty isAllMandatoryFill;
    private UserFreeInputs userFreeInputs;
    private FlowExecutionController flowExecutionController;
    private Boolean isReRunExecution;


    public CollectFreeInputsController() {
        isAllMandatoryFill = new SimpleBooleanProperty(false);
        mandatoryInputsTextField = new ArrayList<>();
    }

    @FXML
    private void initialize() {
        startExecuteButton.disableProperty().bind(isAllMandatoryFill.not());
    }

    public void setReRunExecution(Boolean reRunExecution) {
        isReRunExecution = reRunExecution;
    }


    public void setFlowExecutionController(FlowExecutionController flowExecutionController) {
        this.flowExecutionController = flowExecutionController;
    }

    public void setUserFreeInputs(UserFreeInputs userFreeInputs) {

        this.userFreeInputs = userFreeInputs;
    }

    public void setFlow(FlowDefinition flow) {
        this.flow = flow;
    }

    public void setFlowExecutionToRerun(FlowExecution flowExecutionToRerun) {
        this.flowExecutionToRerun = flowExecutionToRerun;
    }

    @Override
    public void fillScene() {
        // Clear previous data
        freeInputsVbox.getChildren().clear();
        isAllMandatoryFill.set(false);

        if(flow == null) {
            freeInputsVbox.getChildren().add(new Label("No flow has been selected"));
        }
        else {

            if (isReRunExecution)
                rerunExecution();
            else {

                List<IODefinitionData> freeMandatory = userFreeInputs.getMandatoryFreeInputs();
                List<IODefinitionData> freeOptional = userFreeInputs.getOptionalFreeInputs();

                // Add mandatory inputs
                if (!freeMandatory.isEmpty()) {
                    Label mandatoryTitle = new Label("Mandatory:");
                    mandatoryTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                    mandatoryTitle.setStyle("-fx-text-fill: #808080;");
                    freeInputsVbox.getChildren().add(mandatoryTitle);

                    for (IODefinitionData mandatory : freeMandatory) {
                        if (!userFreeInputs.getInitializeValues().contains(mandatory))
                            addInputToScene(mandatory, userFreeInputs);
                    }
                }

                // Add optional inputs
                if (!freeOptional.isEmpty()) {
                    Label optionalTitle = new Label("Optional:");
                    optionalTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                    optionalTitle.setStyle("-fx-text-fill: #808080;");
                    freeInputsVbox.getChildren().add(optionalTitle);

                    for (IODefinitionData optional : freeOptional)
                        if (!userFreeInputs.getInitializeValues().contains(optional))
                            addInputToScene(optional, userFreeInputs);
                }
                freeInputsVbox.setFocusTraversable(true);
                checkAllTextFieldsAreFilled();
            }
        }
    }


    public void addInputToScene(IODefinitionData input, UserFreeInputs freeInputs) {

        // Define the type of data definition
        Class<?> dataDefinitionType = input.getDataDefinition().getType();

        HBox inputHbox = new HBox();
        inputHbox.setSpacing(5);
        inputHbox.setPrefWidth(600);
        inputHbox.setPrefHeight(10);
        Label label = new Label(input.getName() + ": ");
        label.setPrefWidth(100);
        TextField textField = new TextField();
        textField.setPrefWidth(500);

        if(!freeInputs.isDataInserted(input))
            textField.setPromptText("For step " + freeInputs.findStepByInputKey(input).getStepName() + ": " +  input.getUserString());
        else
            textField.setText(freeInputs.getFromInputToObject().get(input).toString());

        inputHbox.getChildren().addAll(label, textField);

        // If the user need to enter a list of arguments
        if(input.getUserString().equals("Command arguments")) {
            textField.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    List<String> arguments = new ArrayList<>();

                    Stage popupStage = new Stage();
                    VBox popupRoot = new VBox();
                    popupRoot.setSpacing(5);

                    TextField argumentField = new TextField();
                    Button addButton = new Button("Add");
                    Button doneButton = new Button("Done");

                    addButton.setOnAction(e -> {
                        String argument = argumentField.getText();
                        if (!argument.isEmpty()) {
                            arguments.add(argument);
                            argumentField.clear();
                        }
                    });

                    doneButton.setOnAction(e -> {
                        String argumentsString = String.join(", ", arguments);
                        textField.setText(argumentsString);
                        popupStage.close();
                    });

                    popupRoot.getChildren().addAll(new Label("Enter argument:"), argumentField, addButton, doneButton);

                    popupStage.setScene(new Scene(popupRoot, 300, 200));
                    popupStage.show();
                }
            });
        }

        // If the object required to be input of file that not exist
        if(input.getUserString().equals("Target file path")) {
            textField.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    TextInputDialog textInputDialog = new TextInputDialog();
                    textInputDialog.setTitle("File Name");
                    textInputDialog.setHeaderText("Enter file name that you want to create and press OK, after choose a directory target");
                    textInputDialog.setContentText("File Name:");

                    Optional<String> result = textInputDialog.showAndWait();
                    result.ifPresent(fileName -> {
                        DirectoryChooser directoryChooser = new DirectoryChooser();
                        directoryChooser.setTitle("Select Directory target");

                        File directory = directoryChooser.showDialog(null);
                        if (directory != null) {
                            String fullPath = directory.getAbsolutePath() + File.separator + fileName;
                            textField.setText(fullPath);
                        }
                    });
                }
            });
        }

        // If the object required to be input of folder
        if(input.getUserString().equals("Folder name to scan")) {
            textField.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    DirectoryChooser directoryChooser = new DirectoryChooser();
                    directoryChooser.setTitle("Select Folder");
                    Stage fileChooserStage = new Stage();
                    File selectedFile = directoryChooser.showDialog(fileChooserStage);

                    if (selectedFile != null) {
                        // Update the text field with the selected file's path
                        textField.setText(selectedFile.getAbsolutePath());
                    }
                }
            });
        }

        // If the input is from class ZipperEnumerator
        if(dataDefinitionType.equals(ZipperEnumerator.class)) {

            textField.setOnMouseClicked(event -> {
                List<String> options = Arrays.asList("ZIP", "UNZIP");

                ChoiceDialog<String> dialog = new ChoiceDialog<>("ZIP", options);
                dialog.setTitle("Choose Action");
                dialog.setHeaderText("Select the action to perform");
                dialog.setContentText("Action:");

                // Show the dialog and wait for user input
                Optional<String> result = dialog.showAndWait();
                result.ifPresent(action -> {
                    if (action.equals("UNZIP"))
                        textField.setText("UNZIP");
                    else {
                        if(action.equals("ZIP"))
                            textField.setText("ZIP");
                    }
                });
            });
        }

        // If the object required to be input of folder or file
        if(input.getUserString().equals("Source")) {
            textField.setOnMouseClicked(event -> {
                List<String> options = Arrays.asList("File", "Directory");

                ChoiceDialog<String> dialog = new ChoiceDialog<>("File", options);
                dialog.setTitle("Choose Option");
                dialog.setHeaderText("Select an option");
                dialog.setContentText("Do you want to choose a file or a directory?");

                Optional<String> result = dialog.showAndWait();
                result.ifPresent(choice -> {
                    Stage stage = new Stage();
                    if (choice.equals("File")) {
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Select a File");
                        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
                        File selectedFile = fileChooser.showOpenDialog(stage);
                        if (selectedFile != null) {
                            textField.setText(selectedFile.getAbsolutePath());
                        }
                    } else if (choice.equals("Directory")) {
                        DirectoryChooser directoryChooser = new DirectoryChooser();
                        directoryChooser.setTitle("Select a Directory");
                        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

                        File selectedDirectory = directoryChooser.showDialog(stage);
                        if (selectedDirectory != null) {
                            textField.setText(selectedDirectory.getAbsolutePath());
                        }
                    }
                });
            });
        }

        // For every input
        textField.textProperty().addListener((observable, oldValue, newValue) -> {

            // Check input
            String inputString = textField.getText();
            try {
                freeInputs.scanInput(input, inputString);
                if (input.getNecessity().equals(DataNecessity.MANDATORY)) {
                    checkAllTextFieldsAreFilled();
                }
            } catch (TypeDontMatchException e) {

                // Handle exception when input is wrong.
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("The input inserted is not valid: " + e.getMessage());
                alert.showAndWait();
            }
        });

        // Add to list if mandatory
        if(input.getNecessity() == DataNecessity.MANDATORY)
            mandatoryInputsTextField.add(textField);

        // Add input to vbox
        freeInputsVbox.getChildren().add(inputHbox);

    }

    public void checkAllTextFieldsAreFilled() {
        boolean isAllFilled = true;

        // Check if any of the text fields are empty
        for (TextField textField : mandatoryInputsTextField) {
            if (textField.getText().isEmpty()) {
                isAllFilled = false;
                isAllMandatoryFill.set(false);
                break;
            }
        }

        if(isAllFilled)
            isAllMandatoryFill.set(true);
    }

    @FXML
    void startExecuteFlowButtonSetOnAction(ActionEvent event) {
        flowExecutionController.setFlowExecute(); // Set property to be "true"
        flowExecutionController.startFlowButtonPress();
    }

    public void rerunExecution() {
        List<IODefinitionData> freeMandatory = userFreeInputs.getMandatoryFreeInputs();
        List<IODefinitionData> freeOptional = userFreeInputs.getOptionalFreeInputs();

        Map<String, Object> mandatoryMap = new HashMap<>();

        if(!freeMandatory.isEmpty()) {
            Label mandatoryTitle = new Label("Mandatory:");
            mandatoryTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            mandatoryTitle.setStyle("-fx-text-fill: #808080;");
            freeInputsVbox.getChildren().add(mandatoryTitle);

            for(IODefinitionData mandatory : freeMandatory) {
                Object value = flowExecutionToRerun.getContext().getContextValues().get(mandatory.getName());
                HBox inputHbox = new HBox();
                inputHbox.setSpacing(5);
                inputHbox.setPrefWidth(600);
                inputHbox.setPrefHeight(10);
                Label label = new Label(mandatory.getName() + ": ");
                label.setPrefWidth(100);
                TextField textField = new TextField(value.toString());
                textField.setEditable(false);
                textField.setPrefWidth(500);

                inputHbox.getChildren().addAll(label, textField);

                // Add input to vbox
                freeInputsVbox.getChildren().add(inputHbox);
            }
        }
        if(isOptionalDataInserted(freeOptional)) {
            Label optionalTitle = new Label("Optional:");
            optionalTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            optionalTitle.setStyle("-fx-text-fill: #808080;");
            freeInputsVbox.getChildren().add(optionalTitle);

            for(IODefinitionData optional : freeOptional) {

                Object value = flowExecutionToRerun.getContext().getContextValues().get(optional.getName());

                // Check if data exist in context
                if(value != null) {
                    HBox inputHbox = new HBox();
                    inputHbox.setSpacing(5);
                    inputHbox.setPrefWidth(600);
                    Label label = new Label(optional.getName() + ": ");
                    TextField textField = new TextField(value.toString());
                    textField.setEditable(false);
                    textField.setPrefWidth(450);
                    inputHbox.getChildren().addAll(label, textField);

                    // Add input to vbox
                    freeInputsVbox.getChildren().add(inputHbox);
                }
            }
        }
        checkAllTextFieldsAreFilled();
    }

    public boolean isOptionalDataInserted(List<IODefinitionData> optionals) {
        for(IODefinitionData optional : optionals) {
            if(userFreeInputs.isDataInserted(optional))
                return true;
        }
        return false;
    }

}
