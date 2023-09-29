package ru.itmo.grafix.colorSpace;

import ru.itmo.grafix.api.ColorSpaceType;

public class YCbCr601 extends YCbCr {
    public YCbCr601() {
        super(ColorSpaceType.YCBCR601);
    }

    @Override
    protected float getLuma(float r, float g, float b) {
        return 0.257f * r + 0.504f * g + 0.098f * b;
    }

    @Override
    protected float getChromaticBlue(float r, float g, float b) {
        return -0.148f * r - 0.291f * g + 0.439f * b;
    }

    @Override
    protected float getChromaticRed(float r, float g, float b) {
        return 0.439f * r - 0.368f * g - 0.071f * b;
    }

    @Override
    protected float getR(float y, float cb, float cr) {
        return 1.164f * y + 1.596f * cr;
    }

    @Override
    protected float getG(float y, float cb, float cr) {
        return 1.164f * y - 0.813f * cr - 0.391f * cb;
    }

    @Override
    protected float getB(float y, float cb, float cr) {
        return 1.164f * y + 2.018f * cb;
    }
}
