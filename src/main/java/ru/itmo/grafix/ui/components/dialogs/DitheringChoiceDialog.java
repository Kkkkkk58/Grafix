package ru.itmo.grafix.ui.components.dialogs;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.layout.VBox;
import ru.itmo.grafix.core.dithering.Dithering;

import java.util.List;

public class DitheringChoiceDialog extends ChoiceDialog<Dithering> {
    public DitheringChoiceDialog(List<Dithering> ditheringList, CheckBox previewCheckbox) {
        super(null, ditheringList);
        setSelectedItem(null);
        setTitle("Dithering methods choice");
        setHeaderText(null);
        setGraphic(null);
        setContentText("Choose the dithering method");
        VBox vbox = new VBox();
        vbox.getChildren().add(getDialogPane().getContent());
        vbox.getChildren().add(previewCheckbox);
        getDialogPane().setContent(vbox);
    }
}
