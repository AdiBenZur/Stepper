package client.user.GUI;

import client.user.GUI.body.UserBodyController;
import client.user.GUI.header.UserHeaderController;
import client.user.GUI.login.LoginController;
import client.user.util.HttpUserUtil;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import servlets.util.Constants;

import java.io.IOException;

public class UserAppController {

    @FXML
    private Parent headerComponent;
    @FXML
    private UserHeaderController headerComponentController;
    @FXML
    private Parent bodyComponent;
    @FXML
    private UserBodyController bodyComponentController;
    @FXML
    private Parent loginComponent;
    @FXML
    private LoginController loginComponentController;

    @FXML
    public void initialize() {



        if (headerComponentController != null && bodyComponentController != null && loginComponentController != null) {
            headerComponentController.setAppController(this);
            bodyComponentController.setAppController(this);
            loginComponentController.setAppController(this);
        }

        headerComponent.setVisible(false);
        bodyComponent.setVisible(false);
        loginComponent.setVisible(true);
    }


    public void registerButtonPress() {
        headerComponent.setVisible(true);
        bodyComponent.setVisible(true);
        loginComponent.setVisible(false);
        bodyComponentController.userJustEnteredToSystem();
    }

    public void setUsernameInHeader(String username) {
        headerComponentController.setUsername(username);
    }

    public void flowDefinitionButtonIsPress() {
        bodyComponentController.flowDefinitionButtonIsPress();
    }

    public void flowExecutionButtonPressFromHeader() {
        bodyComponentController.FlowExecutionButtonPressFromHeader();
    }

    public void executionHistoryButtonPress() {
        bodyComponentController.executionHistoryButtonPress();
    }

    private void login() {

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
                .post(requestBody)
                .build();

        HttpUserUtil.runAsync(request, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            }
        });

    }
}
