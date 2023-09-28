package ru.itmo.grafix.colorSpace;

import ru.itmo.grafix.api.ColorSpace;
import ru.itmo.grafix.api.ColorSpaceType;

// TODO
public class YCbCr709 extends ColorSpace {
    public YCbCr709() {
        super(ColorSpaceType.YCBCR709);
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
