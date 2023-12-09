package ru.itmo.grafix.ui.components.dialogs.filters;

import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import ru.itmo.grafix.core.exception.InvalidFilterParamsException;

public class ThresholdChoiceDialog extends Dialog<int[]> {

    public ThresholdChoiceDialog() {
        setTitle("Threshold params choice");
        setHeaderText(null);
        setGraphic(null);
        setContentText("Choose the threshold count");
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CLOSE);
        getDialogPane().setMinSize(300, 170);


        TextField threshold1 = new TextField();
        TextField threshold2 = new TextField();
        Label threshold2label = new Label("Second threshold");
        gridPane.add(new Label("First threshold"), 0, 1);
        gridPane.add(threshold1, 1, 1);

        ComboBox<Integer> countChoice = new ComboBox<>(FXCollections.observableArrayList(1, 2));
        countChoice.getSelectionModel().selectFirst();
        gridPane.add(new Label("Count of threshold"), 0, 0);
        gridPane.add(countChoice, 1, 0);

        countChoice.setOnAction(e -> {
            if(countChoice.getSelectionModel().getSelectedItem() == 2){
                gridPane.add(threshold2label, 0, 2);
                gridPane.add(threshold2, 1, 2);
            }
            else{
                gridPane.getChildren().remove(threshold2);
                gridPane.getChildren().remove(threshold2label);
            }
        });

        getDialogPane().setContent(gridPane);
        setResultConverter(button ->
        {
            if (!ButtonType.OK.equals(button)){
                return null;
            }
            int t1 = Integer.parseInt(threshold1.getText());
            if(t1 < 0 || t1 > 255){
                throw new InvalidFilterParamsException();
            }
            if(countChoice.getSelectionModel().getSelectedItem() == 1){
                return new int[] {t1};
            }
            int t2 = Integer.parseInt(threshold2.getText());
            if(t2 < 0 || t2 > 255){
                throw new InvalidFilterParamsException();
            }
            return new int[]{t1, t2};
        });
    }
}
