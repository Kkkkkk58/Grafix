package ru.itmo.grafix.colorSpace;

import ru.itmo.grafix.api.ColorSpace;
import ru.itmo.grafix.api.ColorSpaceType;

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
}
