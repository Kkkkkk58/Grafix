package ru.itmo.grafix;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
        stage.getIcons().add(new Image("static/assets/icons/icon.png"));
        Thread.currentThread().setUncaughtExceptionHandler(new GrafixExceptionHandler());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}