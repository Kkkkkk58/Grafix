package ru.itmo.grafix.core.filtering;

import ru.itmo.grafix.core.image.GrafixImage;

public abstract class ThresholdFilter extends Filter {
    protected ThresholdFilter(FilterType type) {
        super(type);
    }

    @Override
    public GrafixImage apply(GrafixImage image) {
        double[] thresholds = getThresholds();

        float[] buffer = new float[image.getTotalChannels() * image.getWidth() * image.getHeight()];
        for (int i = 0; i < buffer.length; ++i) {
            buffer[i] = getThresholdedValue(image.getData()[i], thresholds);
        }

        return new GrafixImage(
                image.getFormat(),
                image.getWidth(),
                image.getHeight(),
                image.getMaxVal(),
                buffer,
                image.getPath(),
                image.getHeaderSize(),
                image.getColorSpace()
        );
    }

    private float getThresholdedValue(float value, double[] thresholds) {
        if (value < thresholds[0]) {
            return 0;
        }
        if (value >= thresholds[thresholds.length - 1]) {
            return 1;
        }
        for (int i = 0; i < thresholds.length - 1; ++i) {
            if (value >= thresholds[i] && value < thresholds[i + 1]) {
                return (float) ((thresholds[i] + thresholds[i + 1]) / 2.0f);
            }
        }
        return 0;
    }

    protected abstract double[] getThresholds();
}
