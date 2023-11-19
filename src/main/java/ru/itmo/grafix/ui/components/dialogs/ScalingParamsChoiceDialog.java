package ru.itmo.grafix.ui.components.dialogs;

import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import ru.itmo.grafix.core.exception.InvalidDrawParamsException;
import ru.itmo.grafix.core.exception.InvalidSizeException;
import ru.itmo.grafix.core.scaling.Scaling;
import ru.itmo.grafix.core.scaling.ScalingType;
import ru.itmo.grafix.ui.models.BCsplineScalingParams;
import ru.itmo.grafix.ui.models.ScalingParams;
import ru.itmo.grafix.ui.models.SimpleScalingParams;

import java.util.List;

public class ScalingParamsChoiceDialog extends Dialog<ScalingParams> {

    public ScalingParamsChoiceDialog(List<Scaling> scalingList){
        setTitle("Scaling params choice");
        setHeaderText(null);
        setGraphic(null);

        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CLOSE);

        TextField width = new TextField("300");
        TextField height = new TextField("300");
        gridPane.add(new Label("Width | Height"), 0, 0);
        gridPane.add(width, 1, 0);
        gridPane.add(height, 2, 0);
        gridPane.add(new Label("px"), 3, 0);

        TextField biasX = new TextField("0");
        TextField biasY = new TextField("0");
        gridPane.add(new Label("Bias (X and Y)"), 0, 1);
        gridPane.add(biasX, 1, 1);
        gridPane.add(biasY, 2, 1);
        gridPane.add(new Label("px"), 3, 1);

        ComboBox<Scaling> scalingChoice = new ComboBox<>(FXCollections.observableArrayList(scalingList));
        scalingChoice.getSelectionModel().selectFirst();

        gridPane.add(new Label("Scaling algo"), 0, 2);
        gridPane.add(scalingChoice, 1, 2);

        getDialogPane().setContent(gridPane);

        setResultConverter(button -> {
            try {
                return (ButtonType.OK.equals(button))
                        ? getScalingParams(width, height, biasX, biasY, scalingChoice.getValue())
                        : null;
            } catch (NumberFormatException exception) {
                throw new InvalidDrawParamsException();
            }
        });
    }

    private ScalingParams getScalingParams(TextField width, TextField height, TextField biasX, TextField biasY, Scaling scaling) {
        float widthValue = Float.parseFloat(width.getText());
        float heightValue = Float.parseFloat(height.getText());

        if (widthValue < 0 || heightValue < 0) {
            throw new InvalidDrawParamsException();
        }
        if (scaling.getType() != ScalingType.BC_SPLINE) {
            return new SimpleScalingParams(
                    widthValue, heightValue,
                    Float.parseFloat(biasX.getText()), Float.parseFloat(biasY.getText()),
                    scaling);
        }
        Pair<Float, Float> bc = new BCInputDialog().showAndWait().orElse(new Pair<>(0f, 0.5f));
        return new BCsplineScalingParams(widthValue, heightValue,
                Float.parseFloat(biasX.getText()), Float.parseFloat(biasY.getText()),
                scaling, bc.getKey(), bc.getValue());
    }
    public static class BCInputDialog extends Dialog<Pair<Float, Float>> {

        public BCInputDialog() {
            setTitle("BC-spline scaling configurator");
            setHeaderText("Choose params for BC-spline scaling");
            setGraphic(null);
            GridPane gridPane = new GridPane();
            TextField b = new TextField("0");
            TextField c = new TextField("0.5");
            gridPane.setHgap(10);
            gridPane.setVgap(10);
            gridPane.add(new Label("B"), 0, 0);
            gridPane.add(b, 1, 0);
            gridPane.add(new Label("C"), 0, 1);
            gridPane.add(c, 1, 1);
            getDialogPane().setContent(gridPane);
            getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CLOSE);
            setResultConverter(button -> {
                        try {
                            return (ButtonType.OK.equals(button))
                                    ? new Pair<>(Float.parseFloat(b.getText()), Float.parseFloat(c.getText()))
                                    : null;
                        } catch (NumberFormatException exception) {
                            throw new InvalidSizeException();
                        }
                    }
            );
        }
    }
}