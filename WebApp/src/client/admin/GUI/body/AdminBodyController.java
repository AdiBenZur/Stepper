package client.admin.GUI.body;

import client.admin.GUI.AdminAppController;
import client.admin.GUI.body.history.AdminExecutionsHistoryController;
import client.admin.GUI.body.role.management.RolesManagementController;
import client.admin.GUI.body.statistic.StatisticsController;
import client.admin.GUI.body.user.management.UsersManagementController;
import javafx.fxml.FXML;
import javafx.scene.Parent;

public class AdminBodyController {

    @FXML
    private Parent executionsHistoryComponent;
    @FXML
    private AdminExecutionsHistoryController executionsHistoryComponentController;
    @FXML
    private Parent rolesManagementComponent;
    @FXML
    private RolesManagementController rolesManagementComponentController;
    @FXML
    private Parent userManagementComponent;
    @FXML
    private UsersManagementController userManagementComponentController;
    @FXML
    private Parent statisticsComponent;
    @FXML
    private StatisticsController statisticsComponentController;
    @FXML
    private Parent startComponent;

    private AdminAppController appController;


    @FXML
    public void initialize() {
        if (executionsHistoryComponentController != null && rolesManagementComponentController != null && userManagementComponentController != null && statisticsComponentController != null) {
            executionsHistoryComponentController.setBodyController(this);
            rolesManagementComponentController.setBodyController(this);
            userManagementComponentController.setBodyController(this);
            statisticsComponentController.setBodyController(this);
        }

        //startAppComponent.setVisible(true);
        startComponent.setVisible(true);
        executionsHistoryComponent.setVisible(false);
        rolesManagementComponent.setVisible(false);
        userManagementComponent.setVisible(false);
        statisticsComponent.setVisible(false);
    }

    public void setAppController(AdminAppController appController) {
        this.appController = appController;
    }

    public void usersManagementButtonPress() {
        startComponent.setVisible(false);
        executionsHistoryComponent.setVisible(false);
        rolesManagementComponent.setVisible(false);
        userManagementComponent.setVisible(true);
        statisticsComponent.setVisible(false);

        userManagementComponentController.startScene();
    }

    public void rolesManagementButtonPress() {
        startComponent.setVisible(false);
        executionsHistoryComponent.setVisible(false);
        rolesManagementComponent.setVisible(true);
        userManagementComponent.setVisible(false);
        statisticsComponent.setVisible(false);

        rolesManagementComponentController.activate();
    }

    public void executionHistoryButtonPress() {
        startComponent.setVisible(false);
        executionsHistoryComponent.setVisible(true);
        rolesManagementComponent.setVisible(false);
        userManagementComponent.setVisible(false);
        statisticsComponent.setVisible(false);

        executionsHistoryComponentController.startScene();
    }

    public void statisticsButtonPress() {
        startComponent.setVisible(false);
        executionsHistoryComponent.setVisible(false);
        rolesManagementComponent.setVisible(false);
        userManagementComponent.setVisible(false);
        statisticsComponent.setVisible(true);

        statisticsComponentController.startScene();
    }




}
