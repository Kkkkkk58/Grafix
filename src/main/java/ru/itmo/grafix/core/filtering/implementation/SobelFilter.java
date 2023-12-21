package ru.itmo.grafix.core.filtering.implementation;

import ru.itmo.grafix.core.filtering.FilterType;
import ru.itmo.grafix.core.filtering.GradientCountingFilter;
import ru.itmo.grafix.core.imageprocessing.PixelValueNormalizer;

public class SobelFilter extends GradientCountingFilter {
    private static final int SOBEL_RADIUS = 1;

    public SobelFilter() {
        super(FilterType.SOBEL);
    }

    @Override
    public boolean setParams() {
        setRadius(SOBEL_RADIUS);
        return true;
    }

    @Override
    protected float applyInternal(float[] data) {
        double gradientX = getGradientX(data);
        double gradientY = getGradientY(data);

        return PixelValueNormalizer.normalize((float) Math.sqrt(gradientX * gradientX + gradientY * gradientY));
    }
}
