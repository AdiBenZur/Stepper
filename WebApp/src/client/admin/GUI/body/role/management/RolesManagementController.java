package client.admin.GUI.body.role.management;

import client.admin.GUI.body.AdminBodyController;
import client.admin.util.HttpAdminUtil;
import com.google.gson.Gson;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RolesManagementController {

    @FXML
    private VBox availableRolesVBox;
    @FXML
    private VBox selectedRoleInformationVBox;
    @FXML
    private HBox descriptionArea;
    @FXML
    private Label roleNameTitleLabel;
    @FXML
    private ListSelectionView<String> flowsListSelectionView;
    @FXML
    private ListView<String> assignedUsersListView;
    @FXML
    private Button saveChangesButton;

    private AdminBodyController bodyController;
    private SimpleBooleanProperty isRoleSelected;
    private Role roleSelected;

    // For popup- add new role
    private Stage popupStage;
    private TextField nameTextField;
    private TextField descriptionTextField;
    private ListSelectionView<String> listSelectionView;



    public RolesManagementController() {
        isRoleSelected = new SimpleBooleanProperty(true);
        popupStage = new Stage();
        nameTextField = new TextField();
        descriptionTextField = new TextField();
        listSelectionView = new ListSelectionView<>();
    }


    @FXML
    private void initialize() {
        selectedRoleInformationVBox.visibleProperty().bind(isRoleSelected.not());
        saveChangesButton.visibleProperty().bind(isRoleSelected.not());
    }


    public void setBodyController(AdminBodyController bodyController) {
        this.bodyController = bodyController;
    }


    public void activate() {
        isRoleSelected.set(true);
        getAllRoles();
    }


    public void getAllRoles() {
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
                String jsonArrayOfRoles = response.body().string();
                Gson gson = new Gson();
                Role[] rolesInSystem = gson.fromJson(jsonArrayOfRoles, Role[].class);
                List<Role> rolesInSystemList = (Arrays.asList(rolesInSystem));

                Platform.runLater(() -> {
                    fillSAvailableRolesVBox(rolesInSystemList);
                });
            }
        });
    }


    public void fillSAvailableRolesVBox(List<Role> roles) {
        availableRolesVBox.getChildren().clear();

        // Add system roles to vbox
        ToggleGroup toggleGroup = new ToggleGroup();

        for (Role role : roles) {

            RadioButton radioButton = new RadioButton(role.getName());
            radioButton.setToggleGroup(toggleGroup);

            // For each button, set action
            radioButton.setOnAction(event -> {
                Platform.runLater(() -> {
                    isRoleSelected.set(false);
                    fillRoleInformation(role);
                });
            });

            availableRolesVBox.getChildren().add(radioButton);
        }
    }


    public void fillRoleInformation(Role role) {
        roleSelected = role;

        // Set title
        roleNameTitleLabel.setText("Role: " + role.getName());

        // Description
        descriptionArea.getChildren().clear();
        Text description = new Text("Description: " + role.getDescription());
        descriptionArea.getChildren().add(description);

        // Flows
        String finalUrl = HttpUrl
                .parse(Constants.FLOWS)
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
                String jsonArrayOfFlows = response.body().string();
                Gson gson = new Gson();
                String[] flowsInSystem = gson.fromJson(jsonArrayOfFlows, String[].class);
                List<String> flowsInSystemList = (Arrays.asList(flowsInSystem));

                List<String> availableFlowsThatNotInRole = flowsInSystemList
                        .stream()
                        .filter(flowName -> !role.getAllowedFlows().contains(flowName)).collect(Collectors.toList());

                Platform.runLater(() -> {
                    flowsListSelectionView.setDisable(role.getName().equals("All Flows") || role.getName().equals("Read Only Flows"));

                    flowsListSelectionView.getSourceItems().clear();
                    flowsListSelectionView.getTargetItems().clear();

                    ObservableList<String> availableFlows = FXCollections.observableArrayList(availableFlowsThatNotInRole);
                    ObservableList<String> roleAllowedFlows = FXCollections.observableArrayList(role.getAllowedFlows());

                    flowsListSelectionView.getSourceItems().setAll(availableFlows);
                    flowsListSelectionView.getTargetItems().setAll(roleAllowedFlows);
                });
            }
        });

        // Users
        String finalUrlUsers = HttpUrl
                .parse(Constants.USERS_LIST)
                .newBuilder()
                .build()
                .toString();

        Request requestUsers = new Request.Builder()
                .url(finalUrlUsers)
                .build();

        HttpAdminUtil.runAsync(requestUsers, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfUsers = response.body().string();
                Gson gson = new Gson();
                User[] usersArr = gson.fromJson(jsonArrayOfUsers, User[].class);
                List<User> users = (Arrays.asList(usersArr));

                List<String> usersThatHasRole = new ArrayList<>();

                users.forEach(user -> {
                    List<String> userRoleNames = user.getUserRoles().stream().map(Role::getName).collect(Collectors.toList());
                    if (userRoleNames.contains(role.getName()))
                        usersThatHasRole.add(user.getUserName());
                });

                Platform.runLater(() -> {
                    assignedUsersListView.getItems().clear();

                    ObservableList<String> usersToListView;
                    if (usersThatHasRole.isEmpty())
                        usersToListView = FXCollections.observableArrayList("No users assigned for this role");
                    else
                        usersToListView = FXCollections.observableArrayList(usersThatHasRole);

                    assignedUsersListView.setItems(usersToListView);
                });
            }
        });
    }


    @FXML
    void saveChangesButtonSetOnAction(ActionEvent event) {
        Role roleAfterChanges = new Role(roleSelected.getName(), roleSelected.getDescription(), flowsListSelectionView.getTargetItems());

        Gson gson = new Gson();
        String roleJson = gson.toJson(roleAfterChanges);

        String finalUrl = HttpUrl
                .parse(Constants.ROLES)
                .newBuilder()
                .build()
                .toString();

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), roleJson);

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
                if(response.isSuccessful()) {
                    String updatedRole = response.body().string();
                    Role role = gson.fromJson(updatedRole, Role.class);

                    Platform.runLater( () -> {
                        changeButtonColorFor1Seconds();
                        fillRoleInformation(role);
                    });
                }
                else {
                    Platform.runLater( () -> {
                        showAlert("Error", response.body().toString());
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


    @FXML
    void createNewRoleButtonSetOnAction(ActionEvent event) {
        // get all flows
        String finalUrl = HttpUrl
                .parse(Constants.FLOWS)
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
                String jsonArrayOfFlows = response.body().string();
                Gson gson = new Gson();
                String[] flowsInSystem = gson.fromJson(jsonArrayOfFlows, String[].class);
                List<String> flowsInSystemList = (Arrays.asList(flowsInSystem));

                Platform.runLater(() -> {
                    openPopupWindowOfCreatingNewRole(flowsInSystemList);
                });
            }
        });
    }


    public void openPopupWindowOfCreatingNewRole(List<String> flows) {
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Add new role");

        // Build scene
        VBox root = new VBox();
        root.setSpacing(8);
        root.setPadding(new Insets(10));

        Label titleLabel = new Label("Create a new role!");
        titleLabel.setFont(new Font(16));

        HBox nameBox = new HBox();
        nameBox.setSpacing(8);
        Label nameLabel = new Label("Enter name:");
        nameBox.getChildren().addAll(nameLabel, nameTextField);

        HBox descriptionBox = new HBox();
        descriptionBox.setSpacing(8);
        Label descriptionLabel = new Label("Enter description:");
        descriptionBox.getChildren().addAll(descriptionLabel, descriptionTextField);
        VBox flowsBox = new VBox();
        flowsBox.setSpacing(8);
        Label flowsLabel = new Label("Choose allowed flows:");
        listSelectionView.getSourceItems().addAll(flows);
        flowsBox.getChildren().addAll(flowsLabel, listSelectionView);

        Button createButton = new Button("Create !");
        createButton.setOnAction(event -> createRoleButtonSetOnAction());

        root.getChildren().addAll(titleLabel, nameBox, descriptionBox, flowsBox, createButton);

        Scene popupScene = new Scene(root, 600, 375);
        popupStage.setScene(popupScene);
        popupStage.show();
    }

    private void createRoleButtonSetOnAction() {
        if (validateInput()) {
            Role newRole = createNewRole();

            // Send http request to save the role in manager
            String finalUrl = HttpUrl
                    .parse(Constants.ROLES)
                    .newBuilder()
                    .build()
                    .toString();

            Gson gson = new Gson();
            String json = gson.toJson(newRole);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

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
                    if (response.isSuccessful()) {
                        Platform.runLater(() -> {
                            popupStage.close();
                            activate();
                        });
                    }
                    else {
                        String error = response.body().string();
                        showAlert("Error", error);
                    }
                }
            });
        }
        else {
            showAlert("Error", "Name and description must be filled.");
        }
    }


    private boolean validateInput() {
        String name = nameTextField.getText().trim();
        String description = descriptionTextField.getText().trim();

        return !name.isEmpty() && !description.isEmpty();
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private Role createNewRole() {
        String name = nameTextField.getText().trim();
        String description = descriptionTextField.getText().trim();
        ObservableList<String> selectedFlows = listSelectionView.getTargetItems();

        return new Role(name, description, new ArrayList<>(selectedFlows));
    }

    public void openPopupErrorWindow(String msg) {
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
