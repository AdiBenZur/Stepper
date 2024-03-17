package client.user.main;
import client.user.GUI.UserAppController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class UserApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Client App Stepper");

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/client/user/GUI/userMainScene.fxml");
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(url.openStream());
        Scene scene = new Scene(root, 708, 605);

        UserAppController userAppController = fxmlLoader.getController();
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            userAppController.shutDown();
        });
    }
}
