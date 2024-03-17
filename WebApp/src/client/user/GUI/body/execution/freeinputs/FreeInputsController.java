package client.user.GUI.body.execution.freeinputs;

import client.user.GUI.body.UserBodyController;
import client.user.util.HttpUserUtil;
import com.google.gson.Gson;
import dto.io.IODefinitionDataDTO;
import dto.io.IOValueDTO;
import dto.io.UserFreeInputsDTO;
import javafx.application.Platform;
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
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import servlets.util.Constants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FreeInputsController {

    @FXML
    private VBox freeInputsVbox;
    @FXML
    private Button startExecuteButton;

    private UserBodyController userBodyController;
    private final SimpleBooleanProperty isAllMandatoryFill;
    private List<TextField> mandatoryInputsTextField;
    private UserFreeInputsDTO currentUserFreeInputsDTO = null;
    private boolean isRerun = false;


    public FreeInputsController() {
        isAllMandatoryFill = new SimpleBooleanProperty(false);
        mandatoryInputsTextField = new ArrayList<>();
    }

    public void setRerun(boolean rerun) {
        isRerun = rerun;
    }

    @FXML
    public void initialize() {
        startExecuteButton.disableProperty().bind(isAllMandatoryFill.not());
    }


    public void setBodyController(UserBodyController userBodyController) {
        this.userBodyController = userBodyController;
    }


    public void startScene(UserFreeInputsDTO userFreeInputsDTO) {

        Platform.runLater(() -> {
            freeInputsVbox.getChildren().clear();
            currentUserFreeInputsDTO = userFreeInputsDTO;
            isAllMandatoryFill.set(false);
            if(userFreeInputsDTO == null)
                freeInputsVbox.getChildren().add(new Label("No flow has been selected"));
            else {
                if(isRerun)
                    rerunExecution(userFreeInputsDTO);
                else {
                    List<IODefinitionDataDTO> freeMandatory = userFreeInputsDTO.getMandatoryFreeInputs();
                    List<IODefinitionDataDTO> freeOptional = userFreeInputsDTO.getOptionalFreeInputs();

                    // Add mandatory inputs
                    if (!freeMandatory.isEmpty()) {
                        Label mandatoryTitle = new Label("Mandatory:");
                        mandatoryTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                        mandatoryTitle.setStyle("-fx-text-fill: #808080;");
                        freeInputsVbox.getChildren().add(mandatoryTitle);

                        for (IODefinitionDataDTO mandatory : freeMandatory) {
                            addInputToScene(mandatory, userFreeInputsDTO);
                        }
                    }

                    // Add optional inputs
                    if (!freeOptional.isEmpty()) {
                        Label optionalTitle = new Label("Optional:");
                        optionalTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                        optionalTitle.setStyle("-fx-text-fill: #808080;");
                        freeInputsVbox.getChildren().add(optionalTitle);

                        for (IODefinitionDataDTO optional : freeOptional)
                            addInputToScene(optional, userFreeInputsDTO);
                    }
                    freeInputsVbox.setFocusTraversable(true);
                    checkAllTextFieldsAreFilled();
                }

            }
        });
    }


    public void addInputToScene(IODefinitionDataDTO input, UserFreeInputsDTO userFreeInputsDTO) {

        HBox inputHbox = new HBox();
        inputHbox.setSpacing(5);
        inputHbox.setPrefWidth(600);
        inputHbox.setPrefHeight(10);
        Label label = new Label(input.getName() + ": ");
        label.setPrefWidth(100);
        TextField textField = new TextField();
        textField.setPrefWidth(500);

        if(userFreeInputsDTO.getFromInputToDataInserted().containsKey(input.getName())) {
            // There is value
            textField.setText(userFreeInputsDTO.getFromInputToDataInserted().get(input.getName()));
        }
        else {
            textField.setPromptText("For step " + currentUserFreeInputsDTO.findStepNameByInput(input) + ": " + input.getUserString());
        }

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
        if(input.getType().equals("ZIPPER_ENUMERATOR")) {

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

        // If the input is from class ProtocolEnumerator
        if(input.getUserString().equals("Method")) {
            textField.setOnMouseClicked(event -> {
                List<String> options = Arrays.asList("GET", "POST", "PUT", "DELETE");

                ChoiceDialog<String> dialog = new ChoiceDialog<>("GET", options);
                dialog.setTitle("Choose Action");
                dialog.setHeaderText("Select the request type");
                dialog.setContentText("Action:");

                // Show the dialog and wait for user input
                Optional<String> result = dialog.showAndWait();
                result.ifPresent(action -> {
                    if (action.equals("GET"))
                        textField.setText("GET");
                    else {
                        if(action.equals("POST"))
                            textField.setText("POST");
                        else {
                            if(action.equals("PUT"))
                                textField.setText("PUT");
                            else {
                                if(action.equals("DELETE"))
                                    textField.setText("DELETE");
                            }
                        }
                    }
                });
            });
        }

        // If the input is from class ProtocolEnumerator
        if(input.getUserString().equals("protocol")) {
            textField.setOnMouseClicked(event -> {
                List<String> options = Arrays.asList("http", "https");

                ChoiceDialog<String> dialog = new ChoiceDialog<>("http", options);
                dialog.setTitle("Choose Action");
                dialog.setHeaderText("Select the method");
                dialog.setContentText("Action:");

                // Show the dialog and wait for user input
                Optional<String> result = dialog.showAndWait();
                result.ifPresent(action -> {
                    if (action.equals("http"))
                        textField.setText("http");
                    else {
                        if(action.equals("https"))
                            textField.setText("https");
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

            // Send http request to know if the type inserted is legal
            IOValueDTO ioValueDTO = new IOValueDTO(currentUserFreeInputsDTO.getFlowName(), input, inputString);

            Gson gson = new Gson();
            String roleJson = gson.toJson(ioValueDTO);

            String finalUrl = HttpUrl
                    .parse(Constants.FREE_INPUTS)
                    .newBuilder()
                    .build()
                    .toString();

            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), roleJson);

            Request request = new Request.Builder()
                    .url(finalUrl)
                    .put(requestBody)
                    .build();

            HttpUserUtil.runAsync(request, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if(response.isSuccessful()) {
                        Platform.runLater(() -> {
                            // Insert the data to the map
                            currentUserFreeInputsDTO.addToMapFromInputToDataInserted(input.getName(), inputString);

                            if(input.getNecessity().equals(Constants.MANDATORY))
                                checkAllTextFieldsAreFilled();
                        });
                    }
                    else {
                        // The input is wrong
                        // Handle exception when input is wrong.
                        String error = response.body().string();
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText(null);
                            alert.setContentText("The input inserted is not valid: " + error);
                            alert.showAndWait();

                        });
                    }
                }
            });
        });

        // Add to list if mandatory
        if(input.getNecessity().equals(Constants.MANDATORY))
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


    public void rerunExecution(UserFreeInputsDTO userFreeInputsDTO) {
        List<IODefinitionDataDTO> freeMandatory = userFreeInputsDTO.getMandatoryFreeInputs();
        List<IODefinitionDataDTO> freeOptional = userFreeInputsDTO.getOptionalFreeInputs();

        if(!freeMandatory.isEmpty()) {
            Label mandatoryTitle = new Label("Mandatory:");
            mandatoryTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            mandatoryTitle.setStyle("-fx-text-fill: #808080;");
            freeInputsVbox.getChildren().add(mandatoryTitle);

            for(IODefinitionDataDTO mandatory : freeMandatory) {
                String value = userFreeInputsDTO.getFromInputToDataInserted().get(mandatory.getName());

                HBox inputHbox = new HBox();
                inputHbox.setSpacing(5);
                inputHbox.setPrefWidth(600);
                inputHbox.setPrefHeight(10);
                Label label = new Label(mandatory.getName() + ": ");
                label.setPrefWidth(100);
                TextField textField = new TextField(value);
                textField.setEditable(false);
                textField.setPrefWidth(500);

                inputHbox.getChildren().addAll(label, textField);

                // Add input to vbox
                freeInputsVbox.getChildren().add(inputHbox);
            }
        }

        if(isOptionalDataInserted(freeOptional, userFreeInputsDTO)) {
            Label optionalTitle = new Label("Optional:");
            optionalTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            optionalTitle.setStyle("-fx-text-fill: #808080;");
            freeInputsVbox.getChildren().add(optionalTitle);
            for(IODefinitionDataDTO optional : freeOptional) {

                String value = userFreeInputsDTO.getFromInputToDataInserted().get(optional.getName());

                // Check if data exist in context
                if(value != null) {
                    HBox inputHbox = new HBox();
                    inputHbox.setSpacing(5);
                    inputHbox.setPrefWidth(600);
                    Label label = new Label(optional.getName() + ": ");
                    TextField textField = new TextField(value);
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


    public boolean isOptionalDataInserted(List<IODefinitionDataDTO> optionals, UserFreeInputsDTO userFreeInputsDTO) {
        for (IODefinitionDataDTO optional : optionals) {
            if (userFreeInputsDTO.getFromInputToDataInserted().containsKey(optional.getName()))
                return true;
        }
        return false;
    }


    @FXML
    void startExecuteFlowButtonSetOnAction(ActionEvent event) {
        String finalUrl = HttpUrl
                .parse(Constants.FREE_INPUTS)
                .newBuilder()
                .addQueryParameter(Constants.FLOW_NAME_TO_EXECUTE, currentUserFreeInputsDTO.getFlowName())
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
                if(!response.isSuccessful()) {
                    // The user not allowed to run the flow anymore. print message and clear vbox
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("The current user does not have permission to run the flow.");
                        alert.showAndWait();

                        freeInputsVbox.getChildren().clear();
                        // Move to flow definition scene
                        userBodyController.flowDefinitionButtonIsPress();
                    });
                }
                else {
                    Platform.runLater(() -> {
                        // Move the user inputs
                        userBodyController.startFlowButtonPress(currentUserFreeInputsDTO);
                    });
                }
            }
        });
    }

}
