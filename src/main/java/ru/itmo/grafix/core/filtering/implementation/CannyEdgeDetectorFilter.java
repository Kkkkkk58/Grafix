package ru.itmo.grafix.core.filtering.implementation;

import ru.itmo.grafix.core.filtering.FilterType;
import ru.itmo.grafix.core.filtering.GradientCountingFilter;
import ru.itmo.grafix.core.image.GrafixImage;

public class CannyEdgeDetectorFilter extends GradientCountingFilter {
    private static final int CANNY_RADIUS = 1;
    private static final GaussianFilter gaussianFilter = new GaussianFilter();

    public CannyEdgeDetectorFilter() {
        super(FilterType.CANNY_EDGE_DETECTOR);
    }

    @Override
    public boolean setParams() {
        setRadius(CANNY_RADIUS);
        gaussianFilter.setSigma(CANNY_RADIUS);
        return true;
    }

    @Override
    protected float applyInternal(float[] data) {
        double gradientX = getGradientX(data);
        double gradientY = getGradientY(data);

        double theta = Math.atan2(gradientY, gradientX);

        return data[data.length / 2];
    }

    @Override
    protected GrafixImage preprocessImage(GrafixImage image) {
        GrafixImage grayscaleImage = super.preprocessImage(image);
        return gaussianFilter.apply(grayscaleImage);
    }
}
