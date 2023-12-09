package ru.itmo.grafix.ui.components.dialogs.filters;

import javafx.scene.control.TextInputDialog;
import ru.itmo.grafix.core.exception.InvalidFilterParamsException;

public class SigmaChoiceDialog extends TextInputDialog {
    public SigmaChoiceDialog() {
        setTitle("Sigma choice");
        setHeaderText("Enter sigma");
        setGraphic(null);
    }

    public static Double getSigmaInput() {
        TextInputDialog td = new SigmaChoiceDialog();
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