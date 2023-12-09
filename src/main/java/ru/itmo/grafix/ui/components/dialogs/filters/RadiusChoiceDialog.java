package ru.itmo.grafix.ui.components.dialogs.filters;

import javafx.scene.control.TextInputDialog;
import ru.itmo.grafix.core.exception.InvalidFilterParamsException;

public class RadiusChoiceDialog extends TextInputDialog {
    public RadiusChoiceDialog() {
        setTitle("Radius choice");
        setHeaderText("Enter radius");
        setGraphic(null);
        getEditor().setText("1");
    }

    public static Integer getRadiusInput() {
        TextInputDialog td = new RadiusChoiceDialog();
        String inputValue = td.showAndWait().orElse(null);
        if (inputValue == null) {
            return null;
        }
        try {
            int result = Integer.parseInt(inputValue);
            if (result <= 0) {
                throw new InvalidFilterParamsException();
            }
            return result;
        } catch (NumberFormatException e) {
            throw new InvalidFilterParamsException();
        }
    }
}
