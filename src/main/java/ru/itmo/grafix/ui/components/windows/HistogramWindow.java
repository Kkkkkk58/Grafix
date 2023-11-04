package ru.itmo.grafix.ui.components.windows;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.itmo.grafix.core.autocorrection.ImageHistogramData;
import ru.itmo.grafix.core.image.GrafixImage;

import java.util.List;

public class HistogramWindow extends Stage {
    private final int CHART_WIDTH = 300;
    private final int CHART_HEIGHT = 150;
    private final String DEFAULT_COLOR = "rgb(128,128,128)";
    private final String COLOR_STYLE = "-fx-stroke: %s; ";
    private final GrafixImage image;

    public HistogramWindow(List<ImageHistogramData> histData, GrafixImage image) {
        this.image = image;
        initModality(Modality.NONE);
        setChartScene(histData);
    }

    public void updateHistogram(List<ImageHistogramData> histData) {
        setChartScene(histData);
    }

    private void setChartScene(List<ImageHistogramData> histData) {
        VBox histList = new VBox();
        Axis<Number> x = new NumberAxis(0, histData.get(0).getData().length, 1);
        x.setTickLabelsVisible(false);
        int currentChannel = image.getChannel();
        if (currentChannel == 0) {
            currentChannel = 1;
        }
        for (ImageHistogramData hist : histData) {
            Axis<Number> y = new NumberAxis();
            y.setTickLabelsVisible(false);
            LineChart<Number, Number> barChart = new LineChart<>(x, y);
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            for (int i = 0; i < hist.getData().length; ++i) {
                series.getData().add(new XYChart.Data<>(i, 0));
                series.getData().add(new XYChart.Data<>(i, hist.getData()[i]));
                series.getData().add(new XYChart.Data<>(i, 0));
            }
            barChart.getData().add(series);
            setChartStyle(barChart, series, currentChannel);
            barChart.setCreateSymbols(false);

            ++currentChannel;
            histList.getChildren().add(barChart);
        }
        setScene(new Scene(histList, CHART_WIDTH, histData.size() * CHART_HEIGHT));
    }

    private void setChartStyle(LineChart<Number, Number> chart, XYChart.Series<Number, Number> series, int currentChannel) {
        Node node = series.getNode();
        if (currentChannel != image.getTotalChannels() + 1) {
            String chartStyleColor = getStringChannelColorInRGB(currentChannel);
            if (node != null) {
                node.setStyle(COLOR_STYLE.formatted(chartStyleColor));
            }
            chart.setTitle("Channel " + currentChannel);
        } else {
            node.setStyle(COLOR_STYLE.formatted(DEFAULT_COLOR));
            chart.setTitle("All");
        }
    }
    private String getStringChannelColorInRGB(int channel) {
        float[] color = new float[]{0.0f, 0.0f, 0.0f};
        color[channel - 1] = 1.0f;
        float[] rgbColor = image.getColorSpace().toRGB(color);
        return colorToStyleString(rgbColor);
    }

    private String colorToStyleString(float[] color) {
        StringBuilder sb = new StringBuilder();
        sb.append("rgb(");
        for (float channel : color) {
            sb.append((int)(channel * 255)).append(',');
        }
        sb.delete(sb.length() - 1, sb.length());
        sb.append(")");
        return sb.toString();
    }
}
