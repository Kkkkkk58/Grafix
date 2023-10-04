package ru.itmo.grafix.ui.components.dialogs;

import javafx.scene.control.ChoiceDialog;
import ru.itmo.grafix.core.colorspace.ColorSpace;

import java.util.List;

public class ColorSpaceChoiceDialog extends ChoiceDialog<ColorSpace> {
    public ColorSpaceChoiceDialog(ColorSpace defaultColorSpace, List<ColorSpace> colorSpaces) {
        super(defaultColorSpace, colorSpaces);
        setTitle("Color spaces choice");
        setHeaderText(null);
        setGraphic(null);
        setContentText("Choose the color space");
    }
}
