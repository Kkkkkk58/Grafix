package ru.itmo.grafix.colorSpace;

import ru.itmo.grafix.api.ColorSpaceType;

public class YCbCr709 extends YCbCr {
    public YCbCr709() {
        super(ColorSpaceType.YCBCR709);
    }

    @Override
    protected float getLuma(float r, float g, float b) {
        return 0.2126f * r + 0.7152f * g + 0.0722f * b;
    }

    @Override
    protected float getChromaticBlue(float r, float g, float b) {
        return -0.1146f * r - 0.3854f * g + 0.5f * b;
    }

    @Override
    protected float getChromaticRed(float r, float g, float b) {
        return 0.5f * r - 0.4542f * g - 0.0458f * b;
    }

    @Override
    protected float getR(float y, float cb, float cr) {
        return y + 1.5748f * cr;
    }

    @Override
    protected float getG(float y, float cb, float cr) {
        return y - 0.4681f * cr - 0.1873f * cb;
    }

    @Override
    protected float getB(float y, float cb, float cr) {
        return y + 1.8556f * cb;
    }
}
