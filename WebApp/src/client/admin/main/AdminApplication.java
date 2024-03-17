package client.admin.main;

import client.admin.GUI.AdminAppController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class AdminApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Admin App Stepper");

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/client/admin/GUI/adminMainScene.fxml");
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(url.openStream());
        Scene scene = new Scene(root, 708, 605);

        primaryStage.setScene(scene);
        AdminAppController controller = fxmlLoader.getController();
        controller.setPrimaryStage(primaryStage);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            controller.shutDown();
        });
    }
}
