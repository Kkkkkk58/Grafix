package ru.itmo.grafix.ui.components.dialogs;

import javafx.scene.control.TextInputDialog;

public class GammaInputDialog extends TextInputDialog {
    public GammaInputDialog(String title, float gamma) {
        setTitle(title);
        setHeaderText("Enter gamma");
        setGraphic(null);
        getEditor().setText(String.valueOf(gamma));
    }
}
