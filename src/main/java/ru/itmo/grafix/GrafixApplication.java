package ru.itmo.grafix;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.itmo.grafix.exception.GrafixExceptionHandler;

import java.io.IOException;

public class GrafixApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GrafixApplication.class.getResource("main-screen-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Grafix");
        stage.setScene(scene);
        Thread.currentThread().setUncaughtExceptionHandler(new GrafixExceptionHandler());
        MainSceneController controller = fxmlLoader.getController();
        ShortcutRegisterService.registerShortcuts(scene, controller);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}