package ru.itmo.grafix.core.filtering;

import java.util.Set;
import java.util.stream.IntStream;

import ru.itmo.grafix.core.colorspace.ColorSpace;
import ru.itmo.grafix.core.colorspace.implementation.CMY;
import ru.itmo.grafix.core.colorspace.implementation.HSL;
import ru.itmo.grafix.core.colorspace.implementation.HSV;
import ru.itmo.grafix.core.colorspace.implementation.RGB;
import ru.itmo.grafix.core.colorspace.implementation.YCbCr601;
import ru.itmo.grafix.core.colorspace.implementation.YCbCr709;
import ru.itmo.grafix.core.colorspace.implementation.YCoCg;
import ru.itmo.grafix.core.image.GrafixImage;
import ru.itmo.grafix.core.imageprocessing.ChannelDecomposer;
import ru.itmo.grafix.core.imageprocessing.RGBGrayscaleConverter;

public abstract class GradientCountingFilter extends ConvolutionalFilter {

    private static final Set<ColorSpace> AVERAGED_GRAYSCALING = Set.of(new RGB(), new CMY());
    private static final Set<ColorSpace> Y_CHANNEL_GRAYSCALING = Set.of(new YCbCr601(), new YCbCr709(), new YCoCg());
    private static final Set<ColorSpace> LAST_CHANNEL_GRAYSCALING = Set.of(new HSL(), new HSV());

    private static final float[] MASK_X = {-1, 0, 1,
                                            -2, 0, 2,
                                            -1, 0, 1};
    private static final float[] MASK_Y = {1, 2, 1,
                                            0, 0, 0,
                                            -1, -2, -1};

    protected GradientCountingFilter(FilterType type) {
        super(type);
    }

    protected double getGradientX(float[] data) {
        return IntStream.range(0, data.length).mapToDouble(i -> data[i] * MASK_X[i]).sum();
    }

    protected double getGradientY(float[] data) {
        return IntStream.range(0, data.length).mapToDouble(i -> data[i] * MASK_Y[i]).sum();
    }

    @Override
    protected GrafixImage preprocessImage(GrafixImage image) {
        if (image.isGrayscale()) {
            return image;
        }

        ColorSpace colorSpace = image.getColorSpace();
        if (AVERAGED_GRAYSCALING.contains(colorSpace)) {
            return RGBGrayscaleConverter.convertRGBToGrayscale(image);
        } else if (Y_CHANNEL_GRAYSCALING.contains(colorSpace)) {
            GrafixImage imageCopy = image.clone();
            imageCopy.setChannel(1);
            return ChannelDecomposer.getDecomposedImage(image);
        } else {
            GrafixImage imageCopy = image.clone();
            imageCopy.setChannel(3);
            return ChannelDecomposer.getDecomposedImage(image);
        }
    }
}