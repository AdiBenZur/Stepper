package client.user.GUI.login;

import client.user.GUI.UserAppController;
import client.user.util.HttpUserUtil;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import servlets.util.Constants;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField userNameTextField;
    @FXML
    private Button registerButton;

    private SimpleBooleanProperty isUserNameFilled;
    private UserAppController userAppController;

    public LoginController() {
        isUserNameFilled = new SimpleBooleanProperty(false);
    }

    public void setAppController(UserAppController userAppController) {
        this.userAppController = userAppController;
    }

    @FXML
    public void initialize() {
        registerButton.disableProperty().bind(isUserNameFilled.not());

        userNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 0) {
                isUserNameFilled.set(true);
            } else {
                isUserNameFilled.set(false);
            }
        });
    }


    @FXML
    void registerButtonSetOnAction(ActionEvent event) {
        String userName = userNameTextField.getText();
        if(userName.isEmpty()) {
            openPopupErrorWindow("User name is empty. Cannot log in with empty user name.");
        }
        String finalUrl = HttpUrl
                .parse(Constants.LOGIN)
                .newBuilder()
                .addQueryParameter(Constants.USERNAME, userName)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        HttpUserUtil.runAsync(request, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        openPopupErrorWindow("Something went wrong!\n " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            openPopupErrorWindow("Something went wrong!\n " + responseBody)
                    );
                } else {
                    Platform.runLater(() -> {
                        // Send username and switch to main user scene
                        userAppController.registerButtonPress();
                        userAppController.setUsernameInHeader(userName);
                    });
                }
            }
        });
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
