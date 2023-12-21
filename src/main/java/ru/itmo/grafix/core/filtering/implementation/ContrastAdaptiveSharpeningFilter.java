package ru.itmo.grafix.core.filtering.implementation;

import java.util.Arrays;
import java.util.stream.IntStream;

import ru.itmo.grafix.core.filtering.ConvolutionalFilter;
import ru.itmo.grafix.core.filtering.FilterType;
import ru.itmo.grafix.core.imageprocessing.PixelValueNormalizer;
import ru.itmo.grafix.ui.components.dialogs.filters.SharpnessChoiceDialog;

public class ContrastAdaptiveSharpeningFilter extends ConvolutionalFilter {
    private static final int[][] CROSS_DXDY = {{1, 0}, {0, 1}, {1, 1}, {2, 1}, {1, 2}};
    private static final int[][] DIAGONALS_DXDY = {{0, 0}, {2, 0}, {0, 2}, {2, 2}};
    private static final int CAS_RADIUS = 1;

    private double sharpness;
    public ContrastAdaptiveSharpeningFilter() {
        super(FilterType.CONTRAST_ADAPTIVE_SHARPENING);
    }

    public void setSharpness(double sharpness) {
        this.sharpness = sharpness;
    }

    public double getSharpness() {
        return sharpness;
    }

    @Override
    public boolean setParams() {
        Double s = SharpnessChoiceDialog.getSharpnessInput();
        if (s == null) {
            return false;
        }
        sharpness = s;
        setRadius(CAS_RADIUS);
        return true;
    }

    @Override
    protected float applyInternal(float[] data) {
        double[] crossValues = Arrays.stream(CROSS_DXDY).mapToDouble(dxdy -> data[dxdy[1] * getDiameter() + dxdy[0]]).toArray();
        float max = (float) Arrays.stream(crossValues).max().orElse(0.0);
        float min = (float) Arrays.stream(crossValues).min().orElse(0.0);

        double[] diagonalValues = Arrays.stream(DIAGONALS_DXDY).mapToDouble(dxdy -> data[dxdy[1] * getDiameter() + dxdy[0]]).toArray();
        float diagMax = (float) Arrays.stream(diagonalValues).max().orElse(0.0);
        float diagMin = (float) Arrays.stream(diagonalValues).min().orElse(0.0);

        max += diagMax;
        min += diagMin;

        double invertedMax = 1.0 / max;
        double amp = Math.sqrt(invertedMax * Math.min(min, 2 - max));
        double w = -amp * (sharpness * (1.0 / 5.0 - 1.0 / 8.0) + 1.0 / 8.0);
        double[] kernel = {0, w, 0,
                            w, 1, w,
                            0, w, 0};

        float value = (float) (IntStream.range(0, data.length).mapToDouble(i -> kernel[i] * data[i]).sum() / (1 + 4.0 * w));
        return PixelValueNormalizer.normalize(value);
    }
}
