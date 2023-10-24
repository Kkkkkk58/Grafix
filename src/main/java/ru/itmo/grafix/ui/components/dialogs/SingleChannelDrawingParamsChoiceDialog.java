package ru.itmo.grafix.ui.components.dialogs;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ru.itmo.grafix.core.colorspace.ColorSpace;

public class SingleChannelDrawingParamsChoiceDialog extends DrawingParamsChoiceDialog {
    public SingleChannelDrawingParamsChoiceDialog(String format, ColorSpace colorSpace, int channel) {
        super(format, colorSpace, channel);
    }

    @Override
    protected TextField[] getColorTextFields() {
        TextField color = new TextField("0");
        GridPane gridPane = (GridPane) getDialogPane().getContent();
        gridPane.add(new Label("Color (0-255)"), 0, 1);
        gridPane.add(color, 1, 1);

        return new TextField[]{color};
    }
}
