package ru.itmo.grafix.ui.components.dialogs.filters;

import javafx.collections.FXCollections;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;
import ru.itmo.grafix.core.filtering.Filter;

import java.util.List;

public class FilterParamsChoiceDialog extends Dialog<Filter> {
    private final ComboBox<Filter> filterSelection;

    public FilterParamsChoiceDialog(List<Filter> filterList) {
        setTitle("Filters algorithms choice");
        setHeaderText(null);
        setGraphic(null);
        setContentText("Choose the filter");
        VBox vbox = new VBox();
        ComboBox<Filter> filterChoice = new ComboBox<>(FXCollections.observableArrayList(filterList));
        filterChoice.getSelectionModel().selectFirst();
        vbox.getChildren().addAll(filterChoice);
        getDialogPane().setContent(vbox);

        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CLOSE);
        setResultConverter(button -> (ButtonType.OK.equals(button))
                ? filterChoice.getValue() : null
        );

        this.filterSelection = filterChoice;
    }
}
