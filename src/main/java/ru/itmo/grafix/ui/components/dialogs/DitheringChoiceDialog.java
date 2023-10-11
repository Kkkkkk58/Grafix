package ru.itmo.grafix.ui.components.dialogs;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import ru.itmo.grafix.core.dithering.Dithering;

import java.util.List;

public class DitheringChoiceDialog extends ChoiceDialog<Dithering> {
    public DitheringChoiceDialog(List<Dithering> ditheringList){
        super(null, ditheringList);
        setTitle("Dithering methods choice");
        setHeaderText(null);
        setGraphic(null);
        setContentText("Choose the dithering method");
        CheckBox preview = new CheckBox("Preview");
        getDialogPane().setContent(preview);
    }
}
