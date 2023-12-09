package ru.itmo.grafix.ui.components.dialogs.filters;

import javafx.collections.FXCollections;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class ThresholdOtsuChoiceDialog extends Dialog<Integer> {

    public ThresholdOtsuChoiceDialog() {
        setTitle("Threshold params choice");
        setHeaderText(null);
        setGraphic(null);
        setContentText("Choose the threshold count");
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CLOSE);

        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        ComboBox<Integer> countChoice = new ComboBox<>(FXCollections.observableArrayList(1, 2));
        countChoice.getSelectionModel().selectFirst();

        gridPane.add(new Label("Count of threshold"), 0, 0);
        gridPane.add(countChoice, 1, 0);

        getDialogPane().setContent(gridPane);
        setResultConverter(button ->
        {
            if (!ButtonType.OK.equals(button)) {
                return null;
            }
            return countChoice.getSelectionModel().getSelectedItem();
        });
    }
}
