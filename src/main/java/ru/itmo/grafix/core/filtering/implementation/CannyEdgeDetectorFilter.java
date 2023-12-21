package ru.itmo.grafix.core.filtering.implementation;

import java.util.Arrays;
import java.util.stream.IntStream;

import ru.itmo.grafix.core.filtering.FilterType;
import ru.itmo.grafix.core.filtering.GradientCountingFilter;
import ru.itmo.grafix.core.image.GrafixImage;
import ru.itmo.grafix.core.imageprocessing.PixelValueNormalizer;

public class CannyEdgeDetectorFilter extends GradientCountingFilter {
    private static final int CANNY_RADIUS = 1;
    private static final double LOW_THRESHOLD_RATIO = 0.05;
    private static final double HIGH_THRESHOLD_RATIO = 0.09;
    private static final int[][] SURROUNDINGS_DXDY = {{-1, -1}, {0, -1}, {1, -1},
                                                        {-1, 0}, {1, 0},
                                                        {-1, 1}, {0, 1}, {1, 1}};
    private static final GaussianFilter gaussianFilter = new GaussianFilter();

    public CannyEdgeDetectorFilter() {
        super(FilterType.CANNY_EDGE_DETECTOR);
    }

    @Override
    public boolean setParams() {
        setRadius(CANNY_RADIUS);
        gaussianFilter.setSigma(3);
        return true;
    }

    @Override
    protected float applyInternal(float[] data) {
        double gradientX = getGradientX(data);
        double gradientY = getGradientY(data);

        double theta = Math.atan2(gradientY, gradientX);
        if (theta < 0) {
            theta += Math.PI;
        }
        double angle = theta * 180.0 / Math.PI;

        return PixelValueNormalizer.normalize(getNonMaxSuppressedValue(angle, data));
    }

    @Override
    protected GrafixImage preprocessImage(GrafixImage image) {
        GrafixImage grayscaleImage = super.preprocessImage(image);
        return gaussianFilter.apply(grayscaleImage);
    }

    @Override
    protected GrafixImage postprocessImage(GrafixImage image) {
        float[] data = image.getData();
        float highThreshold = (float) (HIGH_THRESHOLD_RATIO * IntStream.range(0, data.length).mapToDouble(i -> data[i]).max().orElse(1.0));
        float lowThreshold = (float) (highThreshold * LOW_THRESHOLD_RATIO);
        float weak = 25.0f / 255.0f;
        float strong = 1.0f;
        for (int i = 0; i < data.length; ++i) {
            if (data[i] >= highThreshold) {
                data[i] = strong;
            } else if (data[i] >= lowThreshold) {
                data[i] = weak;
            } else {
                data[i] = 0.0f;
            }
        }

        applyHysteresis(data, image.getWidth(), image.getHeight(), weak, strong);

        return image;
    }

    private void applyHysteresis(float[] data, int width, int height, float weak, float strong) {
        for (int y = 1; y < height - 1; ++y) {
            for (int x = 1; x < width - 1; ++x) {
                if (data[y * width + x] != weak) {
                    continue;
                }
                if (isSurroundedByStrongPixel(data, x, y, width, strong)) {
                    data[y * width + x] = strong;
                } else {
                    data[y * width + x] = 0;
                }
            }
        }
    }

    private boolean isSurroundedByStrongPixel(float[] data, int x, int y, int width, float strong) {
        return Arrays.stream(SURROUNDINGS_DXDY)
                .mapToDouble(neighbour -> data[(y + neighbour[1]) * width + (x + neighbour[0])])
                .anyMatch(it -> Double.compare(it, strong) == 0);
    }

    private float getNonMaxSuppressedValue(double angle, float[] data) {
        int midIndex = data.length / 2;
        float q = 1.0f;
        float r = 1.0f;

        if (0 <= angle && angle < 22.5 || 157.5 <= angle && angle <= 180) {
            q = data[midIndex + 1];
            r = data[midIndex - 1];
        } else if (22.5 <= angle && angle < 67.5) {
            q = data[midIndex + getDiameter() - 1];
            r = data[midIndex - getDiameter() + 1];
        } else if (67.5 <= angle && angle < 112.5) {
            q = data[midIndex + getDiameter()];
            r = data[midIndex - getDiameter()];
        } else if (112.5 <= angle && angle < 157.5) {
            q = data[midIndex - getDiameter() - 1];
            r = data[midIndex + getDiameter() + 1];
        }

        return (data[midIndex] >= q && data[midIndex] >= r) ? data[midIndex] : 0;
    }
}
