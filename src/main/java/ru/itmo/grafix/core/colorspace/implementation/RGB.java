package ru.itmo.grafix.core.colorspace.implementation;

import ru.itmo.grafix.core.colorspace.ColorSpace;
import ru.itmo.grafix.core.colorspace.ColorSpaceType;

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
    public float getCoefficient() {
        return 0;
    }
}
