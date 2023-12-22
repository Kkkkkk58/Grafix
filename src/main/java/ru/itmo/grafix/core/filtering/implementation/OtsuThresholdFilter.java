package ru.itmo.grafix.core.filtering.implementation;

import java.util.Arrays;
import java.util.stream.IntStream;

import ru.itmo.grafix.core.autocorrection.ImageHistogramExtractor;
import ru.itmo.grafix.core.filtering.FilterType;
import ru.itmo.grafix.core.filtering.ThresholdFilter;
import ru.itmo.grafix.core.image.GrafixImage;
import ru.itmo.grafix.ui.components.dialogs.filters.ThresholdOtsuChoiceDialog;

public class OtsuThresholdFilter extends ThresholdFilter {
    private Integer thresholdCount;
    public OtsuThresholdFilter() {
        super(FilterType.THRESHOLD_OTSU);
    }

    @Override
    public boolean setParams() {
        ThresholdOtsuChoiceDialog choiceDialog = new ThresholdOtsuChoiceDialog();
        thresholdCount = choiceDialog.showAndWait().orElse(null);
        return thresholdCount != null;
    }

    @Override
    protected double[] getThresholds(GrafixImage image) {
        return initThresholdValues(thresholdCount, image);
    }

    private double[] initThresholdValues(Integer thresholdCount, GrafixImage image) {
        return switch (thresholdCount) {
            case 1 -> initSingleThresholdValue(image);
            default -> throw new IllegalArgumentException("Not implemented");
        };
    }

    private double[] initSingleThresholdValue(GrafixImage image) {
        int[] imageHistogram = ImageHistogramExtractor.getImageHistogramData(image).get(0).getData();
        int min = IntStream.range(0, imageHistogram.length).filter(i -> imageHistogram[i] > 0).min().orElse(0);
        int max = IntStream.range(0, imageHistogram.length).filter(i -> imageHistogram[i] > 0).max().orElse(255);
        int m = 0;
        int n = 0;
        for (int t = 0; t <= max - min; ++t) {
            m += t * imageHistogram[min + t];
            n += imageHistogram[min + t];
        }
        float maxSigma = -1;
        int threshold = 0;
        int alpha1 = 0;
        int beta1 = 0;

        for (int t = 0; t < max - min; ++t) {
            alpha1 += t * imageHistogram[min + t];
            beta1 += imageHistogram[min + t];
            float w1 = (float) beta1 / n;
            float a = (float) alpha1 / beta1 - (float) (m - alpha1) / (n - beta1);
            float sigma = w1 * (1 - w1) * a * a;
            if (sigma > maxSigma) {
                maxSigma = sigma;
                threshold = min + t;
            }
        }
        return new double[] {threshold / 255f};
    }
}
