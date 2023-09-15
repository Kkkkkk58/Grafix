package ru.itmo.grafix;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class ImageSavingBeforeClosingConfirmationAlert extends Alert {
    public ImageSavingBeforeClosingConfirmationAlert() {
        super(AlertType.CONFIRMATION, "Save changes before closing?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        setHeaderText(null);
        setGraphic(null);
    }
}
