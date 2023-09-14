package ru.itmo.grafix.exception;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;

import java.lang.reflect.InvocationTargetException;

public class GrafixExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
            Throwable rootException = e;
            if (e.getCause() instanceof InvocationTargetException inv) {
                rootException = inv.getTargetException();
            }
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Exception Occurred");
            alert.setContentText("An unexpected error occurred:\n" + rootException.getMessage());
            alert.showAndWait();
    }
}
