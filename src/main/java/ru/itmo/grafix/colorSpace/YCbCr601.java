package ru.itmo.grafix.colorSpace;

import ru.itmo.grafix.api.ColorSpace;
import ru.itmo.grafix.api.ColorSpaceType;

// TODO
public class YCbCr601 extends ColorSpace {
    public YCbCr601() {
        super(ColorSpaceType.YCBCR601);
    }

    @Override
    public float[] toRGB(float[] buffer) {
        return new float[0];
    }

    @Override
    public float[] fromRGB(float[] buffer) {
        return new float[0];
    }
}
