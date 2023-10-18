package ru.itmo.grafix.ui.components.dialogs;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ru.itmo.grafix.core.exception.InvalidDrawParamsException;
import ru.itmo.grafix.ui.models.DrawingParams;

public class DrawingParamsChoiceDialog extends Dialog<DrawingParams> {
    public DrawingParamsChoiceDialog() {
        setTitle("Drawing params choice");
        setHeaderText(null);
        setGraphic(null);
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        TextField thickness = new TextField("1");
        TextField rColor = new TextField("255");
        TextField gColor = new TextField("255");
        TextField bColor = new TextField("255");
        TextField opacity = new TextField("100");
        gridPane.add(new Label("Thickness"), 0, 0);
        gridPane.add(thickness, 1, 0);
        gridPane.add(new Label("px"), 2, 0);

        gridPane.add(new Label("Colors (R, G, B) 0-255"), 0, 1);
        gridPane.add(rColor, 1, 1);
        gridPane.add(gColor, 2, 1);
        gridPane.add(bColor, 3, 1);

        gridPane.add(new Label("Opacity"), 0, 2);
        gridPane.add(opacity, 1, 2);
        gridPane.add(new Label("%"), 2, 2);

        getDialogPane().setContent(gridPane);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CLOSE);

        setResultConverter(button -> {
            try{
                return (ButtonType.OK.equals(button))
                        ? new DrawingParams(Float.parseFloat(thickness.getText()), getColors(rColor, gColor, bColor), Integer.parseInt(opacity.getText()) / 100f)
                        : null;
            } catch (NumberFormatException exception){
                throw new InvalidDrawParamsException();
            }
        });
    }

    private byte[] getColors(TextField r, TextField g, TextField b){
        return new byte[]{getByte(r), getByte(g), getByte(b)};
    }

    private static byte getByte(TextField color) {
        int c = Integer.parseInt(color.getText());
        if (c < 0 || c > 255){
            throw new InvalidDrawParamsException();
        }
        return (byte) c;
    }
}
