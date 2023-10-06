package ru.itmo.grafix.ui.components.dialogs;

import javafx.scene.control.TextInputDialog;
import ru.itmo.grafix.core.exception.InvalidGammaException;
import ru.itmo.grafix.core.image.GrafixImage;

public class GammaInputDialog extends TextInputDialog {
    public GammaInputDialog(String title, float gamma) {
        setTitle(title);
        setHeaderText("Enter gamma");
        setGraphic(null);
        getEditor().setText(String.valueOf(gamma));
    }

    public static Float getGammaInput(String title, GrafixImage image) {
        if (image == null) {
            return null;
        }
        float gamma = image.getGamma();
        TextInputDialog td = new GammaInputDialog(title, gamma);
        String inputValue = td.showAndWait().orElse(null);
        if (inputValue == null) {
            return null;
        }
        try {
            return Float.parseFloat(inputValue);
        } catch (NumberFormatException e) {
            throw new InvalidGammaException(inputValue);
        }
    }
}
