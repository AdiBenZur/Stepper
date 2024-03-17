package client.admin.GUI.body.user.management;

import client.admin.GUI.body.AdminBodyController;
import client.admin.util.HttpAdminUtil;
import com.google.gson.Gson;
import dto.user.UserDTO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import okhttp3.*;
import org.controlsfx.control.ListSelectionView;
import org.jetbrains.annotations.NotNull;
import role.Role;
import servlets.util.Constants;
import user.User;

import java.io.IOException;
import java.util.*;
import java.util.Timer;
import java.util.stream.Collectors;

public class UsersManagementController {

    public static final int REFRESH_RATE = 500;
    @FXML
    private SplitPane splitPane;
    @FXML
    private VBox availableUsersVBox;
    @FXML
    private VBox userInformationVbox;
    @FXML
    private Button saveChangesButton;
    @FXML
    private Label usernameTitleLabel;
    @FXML
    private CheckBox isManagerCheckBox;
    @FXML
    private ListSelectionView<String> rolesListSelectionView;
    @FXML
    private ListView<String> availableFlowsListView;
    @FXML
    private Label nofExecutionsLabel;

    private AdminBodyController bodyController;
    private Timer timer;
    private TimerTask listRefresher;
    private Timer timerExecution;
    private SimpleBooleanProperty isNotButtonSelected;
    private User selectedUser;
    private ToggleGroup usersToggleGroup;
    private List<String> usersInSystemSoFar; // The names
    private boolean isLabelInside;
    private Label noUsersInSystemLabel;

    public UsersManagementController() {
        isNotButtonSelected = new SimpleBooleanProperty(true);
        usersToggleGroup = new ToggleGroup();
        usersInSystemSoFar = new ArrayList<>();
        noUsersInSystemLabel = new Label("There are no users in the system yet.");
    }


    @FXML
    private void initialize() {
        isNotButtonSelected.set(true);
        userInformationVbox.visibleProperty().bind(isNotButtonSelected.not());
        saveChangesButton.visibleProperty().bind(isNotButtonSelected.not());
    }


    public void startScene() {
        availableUsersVBox.getChildren().clear();
        usersInSystemSoFar.clear();
        usersToggleGroup.getToggles().clear();
        isNotButtonSelected.set(true);
        isLabelInside = false;

        startListRefresher();
    }


    public void setBodyController(AdminBodyController bodyController) {
        this.bodyController = bodyController;
    }


    private void updateUsersList(List<User> usersList) {
        // Update VBox
        Platform.runLater(() -> {
            activate(usersList);
        });
    }


    public void startListRefresher() {
        listRefresher = new UserListRefresher(
                this::updateUsersList);
        timer = new Timer();
        timer.schedule(listRefresher, 0, REFRESH_RATE);
    }

    public void activate(List<User> usersList) {

        // Check if the lists of users are empty
        if(usersList.isEmpty() && usersInSystemSoFar.isEmpty()) {
            if(!isLabelInside) {
                availableUsersVBox.getChildren().add(noUsersInSystemLabel);
                isLabelInside = true;
            }
        }

        for(User user : usersList) {

            if(usersInSystemSoFar.contains(user.getUserName())) {
                // Check if there is different online/ offline
                String toFind = user.getUserName() + (user.getIsOnline() ? " (Offline)" : " (Online)");
                for (Toggle toggle : usersToggleGroup.getToggles()) {
                    if (toggle instanceof RadioButton) {
                        RadioButton radioButton = (RadioButton) toggle;
                        if (radioButton.getText().equals(toFind)) {
                            radioButton.setText(user.toString());
                            break; // Stop searching after the first match is found, assuming no duplicates.
                        }
                    }
                }
            }

            if(!usersInSystemSoFar.contains(user.getUserName())) {
                // There is at least one user
                if(isLabelInside) {
                    isLabelInside = false;
                    availableUsersVBox.getChildren().clear();
                }

                // New user
                RadioButton radioButton = new RadioButton(user.toString());
                radioButton.setToggleGroup(usersToggleGroup);
                usersInSystemSoFar.add(user.getUserName());

                // For each button, set action
                radioButton.setOnAction(event -> {

                    // send http request to get the updated user
                    String finalUrl = HttpUrl
                            .parse(Constants.USER_LAST_UPDATE)
                            .newBuilder()
                            .addQueryParameter(Constants.USERNAME, user.getUserName())
                            .build()
                            .toString();

                    Request request = new Request.Builder()
                            .url(finalUrl)
                            .build();
                    HttpAdminUtil.runAsync(request, new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {

                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if(response.isSuccessful()) {
                                String stringUser = response.body().string();
                                Gson gson = new Gson();
                                User userUpdated = gson.fromJson(stringUser, User.class);
                                Platform.runLater(() -> {
                                    isNotButtonSelected.set(false);
                                    showUserInformation(userUpdated);
                                });
                            }
                        }
                    });
                });

                availableUsersVBox.getChildren().add(radioButton);
            }
        }
    }


    public void showUserInformation(User user) {
        selectedUser = user;


        // Title
        usernameTitleLabel.setText("User: " + user.getUserName());

        // Manger
        isManagerCheckBox.setSelected(user.isManager());

        // Roles
        String finalUrl = HttpUrl
                .parse(Constants.ROLES)
                .newBuilder()
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        HttpAdminUtil.runAsync(request, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    String jsonArrayOfRoles = response.body().string();
                    Gson gson = new Gson();
                    Role[] rolesInSystem = gson.fromJson(jsonArrayOfRoles, Role[].class);
                    List<Role> rolesInSystemList = (Arrays.asList(rolesInSystem));

                    List<String> userRoleName = user.getUserRoles().stream().map(Role::getName).collect(Collectors.toList());
                    List<String> availableRoleNames = new ArrayList<>();

                    for(Role role : rolesInSystemList) {
                        if(!userRoleName.contains(role.getName()))
                            availableRoleNames.add(role.getName());
                    }

                    Platform.runLater(() -> {
                        rolesListSelectionView.getSourceItems().clear();
                        rolesListSelectionView.getTargetItems().clear();

                        ObservableList<String> availableRoles = FXCollections.observableArrayList(availableRoleNames);
                        ObservableList<String> userRoles = FXCollections.observableArrayList(user.getUserRoles()
                                .stream()
                                .filter(role -> !role.getName().equals(Constants.MANAGER_FICTIVE_ROLE_NAME))
                                .map(Role::getName).collect(Collectors.toList()));

                        rolesListSelectionView.getSourceItems().setAll(availableRoles);
                        rolesListSelectionView.getTargetItems().setAll(userRoles);
                    });
                }
            }
        });

        // Flows
        availableFlowsListView.getItems().clear();

        List<String> userFlows = user.getUserRoles()
                .stream()
                .flatMap(role -> role.getAllowedFlows().stream()).distinct().collect(Collectors.toList());

        ObservableList<String> userFlowsForListView;
        if (!userFlows.isEmpty())
            userFlowsForListView = FXCollections.observableArrayList(userFlows);
        else
            userFlowsForListView = FXCollections.observableArrayList("No flows available for user currently");

        availableFlowsListView.setItems(userFlowsForListView);

        UserNumberOfExecutionRefresher userNumberOfExecutionRefresher = new UserNumberOfExecutionRefresher(nofExecutionsLabel);
        timerExecution = new Timer();
        timerExecution.schedule(userNumberOfExecutionRefresher, 1000, 1500);

    }


    @FXML
    void saveChangesButtonSetOnAction(ActionEvent event) {

        UserDTO userDTO = new UserDTO(selectedUser.getUserName(), isManagerCheckBox.isSelected(), rolesListSelectionView.getTargetItems(), selectedUser.getIsOnline(), selectedUser.getUserExecutions());
        Gson gson = new Gson();
        String userDTOUpdated = gson.toJson(userDTO);

        String finalUrl = HttpUrl
                .parse(Constants.USERS)
                .newBuilder()
                .build()
                .toString();

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), userDTOUpdated);

        Request request = new Request.Builder()
                .url(finalUrl)
                .post(requestBody)
                .build();

        HttpAdminUtil.runAsync(request, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    String userString = response.body().string();
                    User userUpdated = gson.fromJson(userString, User.class);

                    Platform.runLater(() -> {
                        changeButtonColorFor1Seconds();
                        showUserInformation(userUpdated);
                    });
                }
                else {
                    Platform.runLater(() -> {
                        openPopupErrorWindow(response.body().toString());
                    });
                }
            }
        });
    }

    private void changeButtonColorFor1Seconds() {
        // Change the color to yellow
        saveChangesButton.setStyle("-fx-background-color: FFD733");

        // Create a timeline animation to revert the color after 1 seconds
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            // Revert the color to the default
            saveChangesButton.setStyle("");
        }));
        timeline.setCycleCount(1); // Run the animation only once
        timeline.play();
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


    ///////////////////////////////////////////////////////////////////////////////

    // Inner class refresher

    public class UserNumberOfExecutionRefresher extends TimerTask {

        private final Label label;

        public UserNumberOfExecutionRefresher(Label label) {
            this.label = label;
        }

        @Override
        public void run() {
            String finalUrl = HttpUrl
                    .parse(Constants.USER_NUMBER_OF_EXECUTION)
                    .newBuilder()
                    .addQueryParameter(Constants.USERNAME, selectedUser.getUserName())
                    .build()
                    .toString();

            Request request = new Request.Builder()
                    .url(finalUrl)
                    .build();

            HttpAdminUtil.runAsync(request, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if(response.isSuccessful()) {
                        String bodyStr = response.body().string();
                        Gson gson = new Gson();
                        Integer nofExecutions = gson.fromJson(bodyStr, Integer.class);
                        Platform.runLater(() -> {
                            label.setText(nofExecutions.toString());
                        });
                    }
                }
            });
        }
    }

}
