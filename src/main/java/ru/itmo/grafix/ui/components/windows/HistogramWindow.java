package ru.itmo.grafix.ui.components.windows;

import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.itmo.grafix.core.autocorrection.ImageHistogramData;

import java.util.List;

public class HistogramWindow extends Stage {
    public HistogramWindow(List<ImageHistogramData> histData) {
        initModality(Modality.NONE);
        VBox histList = new VBox();
        Axis<Number> x = new NumberAxis(0, histData.get(0).getData().length, 1);
        x.setTickLabelsVisible(false);
        for (ImageHistogramData hist : histData) {
            Axis<Number> y = new NumberAxis();
//            y.setTickLabelsVisible(false);
            LineChart<Number, Number> barChart = new LineChart<>(x, y);
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            for (int i = 0; i < hist.getData().length; ++i) {
                series.getData().add(new XYChart.Data<>(i, 0));
                series.getData().add(new XYChart.Data<>(i, hist.getData()[i]));
                series.getData().add(new XYChart.Data<>(i, 0));
            }
            barChart.getData().add(series);
            barChart.setCreateSymbols(false);
            barChart.setTitle("ABOBA");

            histList.getChildren().add(barChart);
        }

        setScene(new Scene(histList, 300, 600));
    }
}
