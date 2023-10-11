package ru.itmo.grafix.ui.components.dialogs;

import javafx.collections.FXCollections;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import ru.itmo.grafix.core.dithering.Dithering;

import java.util.List;

public class DitheringChoiceDialog extends Dialog<Pair<Dithering, Integer>> {
    private final ComboBox<Dithering> ditheringSelection;
    private final ComboBox<Integer> bitDepthSelection;

    public DitheringChoiceDialog(List<Dithering> ditheringList, CheckBox previewCheckbox) {
        setTitle("Dithering methods choice");
        setHeaderText(null);
        setGraphic(null);
        setContentText("Choose the dithering method");
        VBox vbox = new VBox();
        ComboBox<Dithering> ditheringChoice = new ComboBox<>(FXCollections.observableArrayList(ditheringList));
        ditheringChoice.getSelectionModel().clearSelection();
        ComboBox<Integer> bitDepthChoice = new ComboBox<>(FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8));
        bitDepthChoice.getSelectionModel().selectLast();
        vbox.getChildren().addAll(ditheringChoice, bitDepthChoice, previewCheckbox);
        getDialogPane().setContent(vbox);
        vbox.setSpacing(10);

        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CLOSE);
        setResultConverter(button -> (ButtonType.OK.equals(button))
                ? new Pair<>(ditheringChoice.getValue(), bitDepthChoice.getValue())
                : null
        );

        this.ditheringSelection = ditheringChoice;
        this.bitDepthSelection = bitDepthChoice;
    }

    public ComboBox<Dithering> getDitheringSelection() {
        return ditheringSelection;
    }

    public ComboBox<Integer> getBitDepthSelection() {
        return bitDepthSelection;
    }
}
