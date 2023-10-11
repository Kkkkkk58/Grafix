package ru.itmo.grafix.ui.components.dialogs;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class SizeInputDialog extends Dialog<Pair<Integer, Integer>> {

    public SizeInputDialog() {
        setTitle("Big or small?");
        setHeaderText("Choose the size");
        setGraphic(null);
        GridPane gridPane = new GridPane();
        TextField width = new TextField("256");
        TextField height = new TextField("256");
        gridPane.add(new Label("Width"), 0, 0);
        gridPane.add(width, 1, 0);
        gridPane.add(new Label("Height"), 0, 1);
        gridPane.add(height, 1, 1);
        getDialogPane().setContent(gridPane);
        ButtonType confirmSize = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CLOSE);

        setResultConverter(button ->
                (ButtonType.OK.equals(button))
                        ? new Pair<>(Integer.parseInt(width.getText()), Integer.parseInt(height.getText()))
                        : null
        );
    }
}
