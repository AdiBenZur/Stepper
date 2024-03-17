package desktop.header;

import desktop.application.AppController;
import desktop.application.EngineController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class HeaderController {

    @FXML
    private Button flowDefinitionButton;
    @FXML
    private Button flowExecutionButton;
    @FXML
    private Button executionsHistoryButton;
    @FXML
    private Button statisticsButton;
    @FXML
    private TextField currentlyLoadedPathTextField;


    private AppController appController;
    private SimpleStringProperty selectedFilePathProperty;
    private SimpleBooleanProperty isFileSelectedProperty;
    private SimpleBooleanProperty isFlowExecuteProperty;

    private String xmlFilePath = null;


    public HeaderController() {
        selectedFilePathProperty = new SimpleStringProperty();
        isFileSelectedProperty = new SimpleBooleanProperty(false);
        isFlowExecuteProperty = new SimpleBooleanProperty(false);
    }


    @FXML
    private void initialize() {
        currentlyLoadedPathTextField.textProperty().bind(selectedFilePathProperty);

        // Unable clicking the menu buttons before loading xml
        flowDefinitionButton.disableProperty().bind(isFileSelectedProperty.not());
        flowExecutionButton.disableProperty().bind(isFlowExecuteProperty.not());
        executionsHistoryButton.disableProperty().bind(isFlowExecuteProperty.not());
        statisticsButton.disableProperty().bind(isFlowExecuteProperty.not());
    }

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    public void setFlowExecute() {
        isFlowExecuteProperty.set(true);
    }



    @FXML
    void executionsHistoryButtonSetOnAction(ActionEvent event) {
        appController.executionsHistoryButtonPress();
    }


    @FXML
    void flowDefinitionButtonSetOnAction(ActionEvent event) {
        appController.flowDefinitionButtonPress();
    }


    @FXML
    void flowExecutionButtonSetOnAction(ActionEvent event) {
        appController.flowExecutionButtonPress();
    }


    @FXML
    void statisticsButtonSetOnAction(ActionEvent event) {
        appController.statisticsButtonPress();
    }


    @FXML
    void loadFileButtonAction(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select xml file");
        Stage fileChooserStage = new Stage();
        File selectedFile = fileChooser.showOpenDialog(fileChooserStage);

        if(selectedFile == null)
            return;

        xmlFilePath = selectedFile.getAbsolutePath();

        loadSystemFromFile();
        if(xmlFilePath == null)
            return;

        selectedFilePathProperty.set(xmlFilePath);
        isFileSelectedProperty.set(true);
        EngineController.getInstance().setThreadPool();
    }


    public void loadSystemFromFile() {
        List<String> errors;

        // Reset data manager
        EngineController.getInstance().resetManager();

        // Reset step statistics manager
        EngineController.getInstance().resetStepsStatisticsManager();

        // Load data from file
        errors = EngineController.getInstance().loadDataFromXml(xmlFilePath);

        // Open new window with load details
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Xml load results");

        TextArea errorTextArea = new TextArea();
        if(errors.isEmpty()) {
            errorTextArea.appendText("The file loaded successfully!");
        }
        else {
            errorTextArea.appendText("Errors occurred while loading the file!\n");
            for(String error: errors){
                errorTextArea.appendText(error + "\n");
            }
            xmlFilePath = null;
            selectedFilePathProperty.set("");
            isFileSelectedProperty.set(false);
            isFlowExecuteProperty.set(false);
            appController.returnToStartScene();
            resetStepper();
        }

        Scene resultScene = new Scene(errorTextArea, 300, 200);
        stage.setScene(resultScene);
        stage.show();
    }


    public void resetStepper() {
        for(int i = 0; i < EngineController.getInstance().getStepper().getAllFlows().size(); i ++)
            EngineController.getInstance().getStepper().getAllFlows().clear();
    }
}
