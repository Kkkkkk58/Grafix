package ru.itmo.grafix.ui.components.dialogs;

import javafx.scene.control.TextInputDialog;
import ru.itmo.grafix.core.exception.InvalidAutocorrectionException;

public class AutocorrectionInputDialog extends TextInputDialog {
    public AutocorrectionInputDialog() {
        setTitle("Autocorrection");
        setHeaderText("Enter autocorrection parameter [0; 0.5)");
        setGraphic(null);
        getEditor().setText("0");
    }

    public static Double getAutocorrectionInput() {
        TextInputDialog td = new AutocorrectionInputDialog();
        String inputValue = td.showAndWait().orElse(null);
        if (inputValue == null) {
            return null;
        }
        try {
            return Double.parseDouble(inputValue);
        } catch (NumberFormatException e) {
            throw new InvalidAutocorrectionException(inputValue);
        }
    }
}
