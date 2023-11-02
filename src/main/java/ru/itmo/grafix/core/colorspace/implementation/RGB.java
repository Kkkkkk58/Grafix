package ru.itmo.grafix.core.colorspace.implementation;

import ru.itmo.grafix.core.autocorrection.AutoCorrecter;
import ru.itmo.grafix.core.autocorrection.ImageHistogramData;
import ru.itmo.grafix.core.autocorrection.ImageHistogramExtractor;
import ru.itmo.grafix.core.colorspace.ColorSpace;
import ru.itmo.grafix.core.colorspace.ColorSpaceType;
import ru.itmo.grafix.core.image.GrafixImage;

import java.util.Arrays;
import java.util.List;

public class RGB extends ColorSpace {
    public RGB() {
        super(ColorSpaceType.RGB);
    }

    @Override
    public float[] toRGB(float[] buffer) {
        return buffer;
    }

    @Override
    public float[] fromRGB(float[] buffer) {
        return buffer;
    }

    @Override
    public int[] getAutocorrectionChannels() {
        return new int[]{0, 1, 2};
    }

    @Override
    public float getCoefficient() {
        return 0;
    }
}
