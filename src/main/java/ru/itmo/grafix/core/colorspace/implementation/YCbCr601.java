package ru.itmo.grafix.core.colorspace.implementation;

import ru.itmo.grafix.core.colorspace.ColorSpaceType;

public class YCbCr601 extends YCbCr {
    public YCbCr601() {
        super(ColorSpaceType.YCBCR601);
    }

    @Override
    protected float getLuma(float r, float g, float b) {
        return 0.299f * r + 0.587f * g + 0.114f * b;
    }

    @Override
    protected float getChromaticBlue(float r, float g, float b) {
        return -0.168736f * r - 0.331264f * g + 0.5f * b;
    }

    @Override
    protected float getChromaticRed(float r, float g, float b) {
        return 0.5f * r - 0.418688f * g - 0.081312f * b;
    }

    @Override
    protected float getR(float y, float cb, float cr) {
        return y + 1.402f * cr;
    }

    @Override
    protected float getG(float y, float cb, float cr) {
        return y - 0.344136f * cb - 0.714136f * cr;
    }

    @Override
    protected float getB(float y, float cb, float cr) {
        return y + 1.772f * cb;
    }
}
