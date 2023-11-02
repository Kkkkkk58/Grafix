package ru.itmo.grafix.ui.components.dialogs;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import ru.itmo.grafix.core.exception.InvalidSizeException;

public class SizeInputDialog extends Dialog<Pair<Integer, Integer>> {

    public SizeInputDialog() {
        setTitle("Big or small?");
        setHeaderText("Choose the size");
        setGraphic(null);
        GridPane gridPane = new GridPane();
        TextField width = new TextField("1920");
        TextField height = new TextField("1080");
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(new Label("Width"), 0, 0);
        gridPane.add(width, 1, 0);
        gridPane.add(new Label("Height"), 0, 1);
        gridPane.add(height, 1, 1);
        getDialogPane().setContent(gridPane);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CLOSE);
        setResultConverter(button -> {
                    try {
                        return (ButtonType.OK.equals(button))
                                ? new Pair<>(Integer.parseInt(width.getText()), Integer.parseInt(height.getText()))
                                : null;
                    } catch (NumberFormatException exception) {
                        throw new InvalidSizeException();
                    }
                }
        );
    }
}
