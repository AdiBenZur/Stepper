package client.admin.GUI;

import client.admin.GUI.body.AdminBodyController;
import client.admin.GUI.header.AdminHeaderController;
import client.admin.util.HttpAdminUtil;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import servlets.util.Constants;

import java.io.IOException;

public class AdminAppController {
    @FXML
    private Parent headerComponent;
    @FXML
    private AdminHeaderController headerComponentController;
    @FXML
    private Parent bodyComponent;
    @FXML
    private AdminBodyController bodyComponentController;
    private Stage primaryStage;

    @FXML
    public void initialize() {

        adminLogin();

        if (headerComponentController != null && bodyComponentController != null) {
            headerComponentController.setAppController(this);
            bodyComponentController.setAppController(this);
        }
    }


    private void adminLogin() {
        String finalUrl = HttpUrl
                .parse(Constants.LOGIN)
                .newBuilder()
                .addQueryParameter("login", "login")
                .build()
                .toString();

        Gson gson = new Gson();
        String json = gson.toJson(new String("Dummy"));
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
                .url(finalUrl)
                .put(requestBody)
                .build();

        HttpAdminUtil.runAsync(request, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        showErrorAndExit("An error occurred. Application will closed.")
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            showErrorAndExit(responseBody)
                    );
                }
            }
        });
    }

    private void showErrorAndExit(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);
        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                // Close the application
                primaryStage.close();
            }
        });
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }



    public void shutDown() {
        String finalUrl = HttpUrl
                .parse(Constants.LOGIN)
                .newBuilder()
                .addQueryParameter("login", "logout")
                .build()
                .toString();

        Gson gson = new Gson();
        String json = gson.toJson(new String("Dummy"));
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
                .url(finalUrl)
                .put(requestBody)
                .build();

        HttpAdminUtil.runAsync(request, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            }
        });
    }




    public void usersManagementButtonPress() {
        bodyComponentController.usersManagementButtonPress();
    }

    public void rolesManagementButtonPress() {
        bodyComponentController.rolesManagementButtonPress();
    }

    public void executionsHistoryButtonPress() {
        bodyComponentController.executionHistoryButtonPress();
    }

    public void statisticsButtonPress() {
        bodyComponentController.statisticsButtonPress();
    }
}
