package ru.itmo.grafix.ui.components.dialogs;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class P5DrawingParamsChoiceDialog extends DrawingParamsChoiceDialog {
    @Override
    protected TextField[] getColorTextFields() {
        TextField color = new TextField("0");
        GridPane gridPane = (GridPane) getDialogPane().getContent();
        gridPane.add(new Label("Color (0-255)"), 0, 1);
        gridPane.add(color, 1, 1);

        return new TextField[]{color};
    }
}
