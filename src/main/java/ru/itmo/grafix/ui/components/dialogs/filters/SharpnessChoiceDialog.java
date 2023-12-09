package ru.itmo.grafix.ui.components.dialogs.filters;

import javafx.scene.control.TextInputDialog;
import ru.itmo.grafix.core.exception.InvalidFilterParamsException;

public class SharpnessChoiceDialog extends TextInputDialog {
    public SharpnessChoiceDialog() {
        setTitle("Sharpness choice");
        setHeaderText("Enter sharpness");
        setGraphic(null);
        getEditor().setText("0");
    }

    public static Double getSharpnessInput() {
        TextInputDialog td = new SharpnessChoiceDialog();
        String inputValue = td.showAndWait().orElse(null);
        if (inputValue == null) {
            return null;
        }
        try {
            double result = Double.parseDouble(inputValue);
            if (result < 0 || result > 1) {
                throw new InvalidFilterParamsException();
            }
            return result;
        } catch (NumberFormatException e) {
            throw new InvalidFilterParamsException();
        }
    }
}
