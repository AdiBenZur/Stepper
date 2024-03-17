package desktop.application;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;


public class DesktopApplication extends Application {

    private final EngineController engineController = new EngineController();


    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Stepper");

        Parent load = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("mainScene.fxml")));
        Scene scene = new Scene(load, 708, 505);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Shut down thread pool
        primaryStage.setOnCloseRequest(e -> {
            EngineController.getInstance().getExecuteFlowManager().shutDown();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

}
