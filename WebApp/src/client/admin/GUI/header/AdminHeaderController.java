package client.admin.GUI.header;

import client.admin.GUI.AdminAppController;
import client.user.util.HttpUserUtil;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import servlets.util.Constants;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AdminHeaderController {

    @FXML
    private TextField currentLoadedFilePathTextField;
    @FXML
    private Button userManagementButton;
    @FXML
    private Button roleManagementButton;
    @FXML
    private Button executionsHistoryButton;
    @FXML
    private Button statisticsButton;
    private AdminAppController appController;
    private String xmlFilePath = null;
    private SimpleBooleanProperty isFileInserted;


    public AdminHeaderController() {
        isFileInserted = new SimpleBooleanProperty(false);
    }

    public void setAppController(AdminAppController appController) {
        this.appController = appController;
    }


    @FXML
    public void initialize() {
        currentLoadedFilePathTextField.setEditable(false);


    }


    @FXML
    void loadFileButtonSetOnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select xml file");
        Stage fileChooserStage = new Stage();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(fileChooserStage);

        if(selectedFile == null)
            return;

        xmlFilePath = selectedFile.getAbsolutePath();

        RequestBody body = new MultipartBody.Builder()
                .addFormDataPart("file", selectedFile.getName(), RequestBody.create(selectedFile, MediaType.parse("text/plain")))
                .build();

        Request request = new Request.Builder()
                .url(Constants.UPLOAD_FILE)
                .post(body)
                .build();

        HttpUserUtil.runAsync(request, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        openPopupErrorWindow("Something went wrong. Could not connect to server!"));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    String result = response.body().string();
                    if(result.equals("New flows added to the system successfully!")) {
                        Platform.runLater(() -> {
                            currentLoadedFilePathTextField.setText(xmlFilePath);
                            openPopupErrorWindow(result);
                        });
                    }
                    else {
                        Platform.runLater(() -> {
                            openPopupErrorWindow(result);
                        });
                    }
                }
                else {
                    if(response.code() == 409) {
                        // There are errors
                        Gson gson = new Gson();
                        String errorsString = response.body().string();
                        String[] errors = gson.fromJson(errorsString, String[].class);
                        List<String> errorsList = Arrays.asList(errors);
                        Platform.runLater(() -> {
                            StringBuilder builder = new StringBuilder();
                            builder.append("Errors occurred while loading the file!\n");
                            for (String error : errorsList)
                                builder.append(error + "\n");

                            openPopupErrorWindow(builder.toString());
                        });
                    }
                }
            }
        });
    }


    @FXML
    void executionsHistoryButtonSetOnAction(ActionEvent event) {
        appController.executionsHistoryButtonPress();
    }

    @FXML
    void roleManagementButtonSetOnAction(ActionEvent event) {
        appController.rolesManagementButtonPress();
    }

    @FXML
    void statisticsButtonSetOnAction(ActionEvent event) {
        appController.statisticsButtonPress();
    }

    @FXML
    void userManagementButtonSetOnAction(ActionEvent event) {
        appController.usersManagementButtonPress();
    }


    public void openPopupErrorWindow(String msg) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Error");

        TextArea textArea = new TextArea(msg);
        textArea.setEditable(false);
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> popupStage.close());

        VBox popupLayout = new VBox(10);
        popupLayout.setAlignment(Pos.CENTER);
        popupLayout.getChildren().addAll(textArea, closeButton);

        popupStage.setScene(new Scene(popupLayout, 400, 200));
        popupStage.showAndWait();
    }
}
