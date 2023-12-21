package ru.itmo.grafix.core.filtering.implementation;

import java.util.stream.IntStream;

import ru.itmo.grafix.core.filtering.ConvolutionalFilter;
import ru.itmo.grafix.core.filtering.FilterType;
import ru.itmo.grafix.ui.components.dialogs.filters.SigmaChoiceDialog;

public class GaussianFilter extends ConvolutionalFilter {
    private double sigma;
    private double[] weightMatrix;

    public GaussianFilter() {
        super(FilterType.GAUSSIAN);
    }

    public void setSigma(double sigma) {
        this.sigma = sigma;
        int radius = (int) Math.ceil(3 * sigma);
        this.setRadius(radius);
    }

    public double getSigma() {
        return sigma;
    }

    @Override
    public boolean setParams() {
        Double s = SigmaChoiceDialog.getSigmaInput();
        if (s == null) {
            return false;
        }
        setSigma(sigma);
        return true;
    }

    @Override
    public void setRadius(int radius) {
        super.setRadius(radius);
        initWeightMatrix();
    }

    @Override
    protected float applyInternal(float[] data) {
        return (float) IntStream.range(0, data.length).mapToDouble(i -> weightMatrix[i] * data[i]).sum();
    }

    private void initWeightMatrix() {
        int diameter = getDiameter();
        double[] matrix = new double[diameter * diameter];
        for (int y = 0; y < diameter; ++y) {
            for (int x = 0; x < diameter; ++x) {
                matrix[y * diameter + x] = getGaussianFunction(x - getRadius(), y - getRadius());
            }
        }
        this.weightMatrix = matrix;
    }

    private double getGaussianFunction(int x, int y) {
        return 1.0 / (2 * Math.PI * sigma * sigma) * Math.exp(-(x * x + y * y) / (2 * sigma * sigma));
    }
}