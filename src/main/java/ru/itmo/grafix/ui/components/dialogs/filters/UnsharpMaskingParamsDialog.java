package ru.itmo.grafix.ui.components.dialogs.filters;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import ru.itmo.grafix.core.exception.InvalidFilterParamsException;
import ru.itmo.grafix.core.exception.InvalidSizeException;
import ru.itmo.grafix.ui.models.UnsharpMaskingParams;

public class UnsharpMaskingParamsDialog extends Dialog<UnsharpMaskingParams> {
    public UnsharpMaskingParamsDialog() {
        setTitle("Choose it.");
        setHeaderText("Choose the params for unsharp masking algorithm");
        setGraphic(null);
        GridPane gridPane = new GridPane();
        TextField amount = new TextField("0");
        TextField radius = new TextField("0.1");
        TextField threshold = new TextField("0");
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(new Label("Amount"), 0, 0);
        gridPane.add(amount, 1, 0);
        gridPane.add(new Label("Radius"), 0, 1);
        gridPane.add(radius, 1, 1);
        gridPane.add(new Label("Threshold"), 0, 2);
        gridPane.add(threshold, 1, 2);
        getDialogPane().setContent(gridPane);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CLOSE);
        setResultConverter(button -> {
                    try {
                        if(!ButtonType.OK.equals(button)) {
                            return null;
                        }
                        double a = Double.parseDouble(amount.getText());
                        double r = Double.parseDouble(radius.getText());
                        int t = Integer.parseInt(threshold.getText());
                        if(a < 0 || a > 5 || r < 0.1 || r > 120 || t < 0 || t > 255){
                            throw new InvalidFilterParamsException();
                        }
                        return new UnsharpMaskingParams(a, r, t);
                    } catch (NumberFormatException exception) {
                        throw new InvalidSizeException();
                    }
                }
        );
    }
}

