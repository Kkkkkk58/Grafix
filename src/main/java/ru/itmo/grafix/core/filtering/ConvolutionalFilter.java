package ru.itmo.grafix.core.filtering;

import ru.itmo.grafix.core.image.GrafixImage;
import ru.itmo.grafix.ui.components.dialogs.filters.RadiusChoiceDialog;

public abstract class ConvolutionalFilter extends Filter {
    private int radius;

    protected ConvolutionalFilter(FilterType type) {
        super(type);
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }

    public int getDiameter() {
        return 2 * radius + 1;
    }

    @Override
    public boolean setParams() {
        Integer r = RadiusChoiceDialog.getRadiusInput();
        if (r == null) {
            return false;
        }
        setRadius(r);
        return true;
    }

    @Override
    public GrafixImage apply(GrafixImage image) {
        image = preprocessImage(image);
        float[] buffer = new float[image.getData().length];
        for(int y = 0; y < image.getHeight(); ++y){
            for(int x = 0; x < image.getWidth(); ++x){
                for (int k = 0; k < image.getTotalChannels(); ++k) {
                    float[] matrix = getMatrixValues(x, y, image.getData(), image.getWidth(), image.getHeight(), k, image.getTotalChannels());
                    float filteredValue = applyInternal(matrix);
                    buffer[image.getTotalChannels() * (y * image.getWidth() + x) + k] = filteredValue;
                }
            }
        }

        image = new GrafixImage(
                image.getFormat(),
                image.getWidth(),
                image.getHeight(),
                image.getMaxVal(),
                buffer,
                image.getPath(),
                image.getHeaderSize(),
                image.getColorSpace()
        );

        return postprocessImage(image);
    }

    protected GrafixImage preprocessImage(GrafixImage image) {
        return image;
    }

    protected GrafixImage postprocessImage(GrafixImage image) {
        return image;
    }

    private float[] getMatrixValues(int centerX, int centerY, float[] data, int width, int height, int channel, int bytesPerPixel) {
        int diameter = getDiameter();
        float[] matrix = new float[diameter * diameter];
        for (int dy = 0; dy < diameter; ++dy) {
            for (int dx = 0; dx < diameter; ++dx) {
                int y = centerY - radius + dy;
                int x = centerX - radius + dx;
                float value;
                if (y < 0) {
                    if (x < 0) {
                        value = data[0 + channel];
                    } else if (x >= width) {
                        value = data[bytesPerPixel * (width - 1) + channel];
                    } else {
                        value = data[x * bytesPerPixel + channel];
                    }
                } else if (y >= height) {
                    if (x < 0) {
                        value = data[data.length - bytesPerPixel * width + channel];
                    } else if (x >= width) {
                        value = data[data.length - 1 * bytesPerPixel + channel];
                    } else {
                        value = data[data.length - bytesPerPixel * (width - x) + channel];
                    }
                } else if (x < 0) {
                    value = data[y * width * bytesPerPixel + channel];
                } else if (x >= width) {
                    value = data[bytesPerPixel * ((y + 1) * width - 1) + channel];
                } else {
                    value = data[bytesPerPixel * (y * width + x) + channel];
                }
                matrix[dy * diameter + dx] = value;
            }
        }
        return matrix;
    }

    protected abstract float applyInternal(float[] data);
}
