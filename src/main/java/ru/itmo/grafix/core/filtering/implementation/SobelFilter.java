package ru.itmo.grafix.core.filtering.implementation;

import java.util.stream.IntStream;

import ru.itmo.grafix.core.filtering.ConvolutionalFilter;
import ru.itmo.grafix.core.filtering.FilterType;
import ru.itmo.grafix.core.imageprocessing.PixelValueNormalizer;

public class SobelFilter extends ConvolutionalFilter {
    private static final int SOBEL_RADIUS = 1;
    private static final float[] MASK_X = {-1, -2, -1,
                                            0, 0, 0,
                                            1, 2, 1};
    private static final float[] MASK_Y = {-1, 0, 1,
                                            -2, 0, 2,
                                            -1, 0, 1};

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
        double gradientX = IntStream.range(0, data.length).mapToDouble(i -> data[i] * MASK_X[i]).sum();
        double gradientY = IntStream.range(0, data.length).mapToDouble(i -> data[i] * MASK_Y[i]).sum();

        // FIXME 3-channel image
        return PixelValueNormalizer.normalize((float) Math.sqrt(gradientX * gradientX + gradientY * gradientY));
    }
}
