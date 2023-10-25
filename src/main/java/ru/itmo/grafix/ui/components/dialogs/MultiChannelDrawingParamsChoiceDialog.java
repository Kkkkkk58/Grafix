package ru.itmo.grafix.ui.components.dialogs;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ru.itmo.grafix.core.colorspace.ColorSpace;

public class MultiChannelDrawingParamsChoiceDialog extends DrawingParamsChoiceDialog {

    public MultiChannelDrawingParamsChoiceDialog(String format, ColorSpace colorSpace, int channel) {
        super(format, colorSpace, channel);
    }

    @Override
    protected TextField[] getColorTextFields() {
        TextField rColor = new TextField("0");
        TextField gColor = new TextField("0");
        TextField bColor = new TextField("0");
        GridPane gridPane = (GridPane) getDialogPane().getContent();
        gridPane.add(new Label("Colors 0-255"), 0, 1);
        gridPane.add(rColor, 1, 1);
        gridPane.add(gColor, 2, 1);
        gridPane.add(bColor, 3, 1);
        return new TextField[]{rColor, gColor, bColor};
    }
}
