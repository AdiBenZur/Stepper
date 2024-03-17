package client.user.GUI.header;

import client.user.GUI.UserAppController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import role.Role;
import servlets.util.Constants;
import user.User;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class UserHeaderController {

    public static final int REFRESH_RATE = 1000;
    public static final String NO_ROLES = "No Roles assigned";

    @FXML
    private Label usernameLabel;
    @FXML
    private CheckBox isManagerCheckBox;
    @FXML
    private Label assignedRolesLabel;
    @FXML
    private Button flowDefinitionButton;
    @FXML
    private Button flowsExecutionsButton;
    @FXML
    private Button executionsHistoryButton;

    private UserAppController userAppController;
    private String username;
    private Timer timer;
    private TimerTask listRefresher;


    public UserHeaderController() {

    }

    public void setAppController(UserAppController userAppController) {
        this.userAppController = userAppController;
    }

    @FXML
    public void initialize() {
        isManagerCheckBox.setDisable(false);
    }

    public void setUsername(String loginName) {
        this.username = loginName;
        usernameLabel.setText(username);

        startListRefresher();
    }


    public void startListRefresher() {
        listRefresher = new UserHeaderDetailsRefresher(
                this::updateChangeableUserDetails);
        timer = new Timer();
        timer.schedule(listRefresher, 0, REFRESH_RATE);
    }


    private void updateChangeableUserDetails(User user) {
        // Roles
        Platform.runLater(() -> {
            List<String> rolesStringList = user.getUserRoles().stream().map(Role::getName).collect(Collectors.toList());
            StringBuilder rolesBuilder = new StringBuilder();
            for(String role : rolesStringList) {
                if(!role.equals(Constants.MANAGER_FICTIVE_ROLE_NAME))
                    rolesBuilder.append(role).append(". ");
            }

            if(rolesBuilder.length() == 0)
                assignedRolesLabel.setText(NO_ROLES);
            else
                assignedRolesLabel.setText(rolesBuilder.toString());

            // Manager
            isManagerCheckBox.setSelected(user.isManager());
        });
    }


    @FXML
    void executionsHistoryButtonSetOnAction(ActionEvent event) {
        userAppController.executionHistoryButtonPress();
    }

    @FXML
    void flowDefinitionButtonSetOnAction(ActionEvent event) {
        userAppController.flowDefinitionButtonIsPress();
    }

    @FXML
    void flowsExecutionsButtonSetOnAction(ActionEvent event) {
        userAppController.flowExecutionButtonPressFromHeader();
    }


}
