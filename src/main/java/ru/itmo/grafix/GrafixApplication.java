package ru.itmo.grafix;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import ru.itmo.grafix.core.exception.GrafixExceptionHandler;
import ru.itmo.grafix.ui.controllers.MainSceneController;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GrafixApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GrafixApplication.class.getResource("main-screen-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        setDragDrop(scene, fxmlLoader.getController());
        stage.setTitle("Grafix");
        stage.setScene(scene);
        stage.getIcons().add(new Image("static/assets/icons/icon.png"));
        Thread.currentThread().setUncaughtExceptionHandler(new GrafixExceptionHandler());
        stage.show();
    }

    private void setDragDrop(Scene scene, MainSceneController controller) {
        scene.getRoot().setOnDragOver(event -> {
            if (event.getGestureSource() != scene.getRoot() && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        scene.getRoot().setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                List<File> files = db.getFiles();
                controller.openDraggedFile(files.get(0));
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    public static void main(String[] args) {
        launch();
    }
}