package ru.itmo.grafix.ui.components.dialogs;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ru.itmo.grafix.core.colorspace.ColorSpace;
import ru.itmo.grafix.core.exception.InvalidDrawParamsException;
import ru.itmo.grafix.ui.models.DrawingParams;

import java.util.Arrays;
import java.util.List;

public abstract class DrawingParamsChoiceDialog extends Dialog<DrawingParams> {
    private final String format;
    private final ColorSpace colorSpace;
    private final int channel;

    protected DrawingParamsChoiceDialog(String format, ColorSpace colorSpace, int channel) {
        this.format = format;
        this.colorSpace = colorSpace;
        this.channel = channel;

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
            try {
                return (ButtonType.OK.equals(button))
                        ? getDrawingParams(thickness, colors, opacity)
                        : null;
            } catch (NumberFormatException exception) {
                throw new InvalidDrawParamsException();
            }
        });
    }

    private DrawingParams getDrawingParams(TextField thickness, TextField[] colors, TextField opacity) {
        return new DrawingParams(
                Float.parseFloat(thickness.getText()),
                getColors(colors),
                Integer.parseInt(opacity.getText()) / 100f);
    }

    protected abstract TextField[] getColorTextFields();

    private float[] getColors(TextField... colors) {
        List<Byte> bytesList = Arrays.stream(colors).map(this::getByte).toList();
        int size = bytesList.size();
        if (!format.equals("P5") && channel != 0) {
            size = 3;
        }
        float[] data = new float[size];
        if (channel == 0) {
            for (int i = 0; i < size; i++) {
                data[i] = (bytesList.get(i) & 0xff) / 255f;
            }
        } else {
            data[channel - 1] = (bytesList.get(0) & 0xff) / 255f;
        }

        return colorSpace.toRGB(data);
    }

    private byte getByte(TextField color) {
        int c = Integer.parseInt(color.getText());
        if (c < 0 || c > 255) {
            throw new InvalidDrawParamsException();
        }
        return (byte) c;
    }
}
