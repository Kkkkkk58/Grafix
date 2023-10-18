package ru.itmo.grafix.ui.components.dialogs;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ru.itmo.grafix.core.exception.InvalidDrawParamsException;
import ru.itmo.grafix.ui.models.DrawingParams;

import java.util.Arrays;
import java.util.List;

public abstract class DrawingParamsChoiceDialog extends Dialog<DrawingParams> {
    protected DrawingParamsChoiceDialog() {
        setTitle("Drawing params choice");
        setHeaderText(null);
        setGraphic(null);
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        getDialogPane().setContent(gridPane);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CLOSE);
        TextField[] colors = getColorTextFields();
        TextField thickness = new TextField("1");
        TextField opacity = new TextField("100");
        gridPane.add(new Label("Thickness"), 0, 0);
        gridPane.add(thickness, 1, 0);
        gridPane.add(new Label("px"), 2, 0);

        gridPane.add(new Label("Opacity"), 0, 2);
        gridPane.add(opacity, 1, 2);
        gridPane.add(new Label("%"), 2, 2);

        setResultConverter(button -> {
            try{
                return (ButtonType.OK.equals(button))
                        ? new DrawingParams(Float.parseFloat(thickness.getText()), getColors(colors), Integer.parseInt(opacity.getText()) / 100f)
                        : null;
            } catch (NumberFormatException exception){
                throw new InvalidDrawParamsException();
            }
        });
    }

    protected abstract TextField[] getColorTextFields();

    private byte[] getColors(TextField... colors){
        List<Byte> bytesList = Arrays.stream(colors).map(this::getByte).toList();
        byte[] bytes = new byte[bytesList.size()];
        for (int i = 0; i < bytesList.size(); i++) {
            bytes[i] = bytesList.get(i);
        }

        return bytes;
    }

    private byte getByte(TextField color) {
        int c = Integer.parseInt(color.getText());
        if (c < 0 || c > 255){
            throw new InvalidDrawParamsException();
        }
        return (byte) c;
    }
}
