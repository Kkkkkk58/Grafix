package ru.itmo.grafix.colorSpace;

import ru.itmo.grafix.api.ColorSpaceType;

public class YCbCr709 extends YCbCr {
    public YCbCr709() {
        super(ColorSpaceType.YCBCR709);
    }

    @Override
    protected float getLuma(float r, float g, float b) {
        return 0.183f * r + 0.614f * g + 0.062f * b;
    }

    @Override
    protected float getChromaticBlue(float r, float g, float b) {
        return -0.101f * r - 0.338f * g + 0.439f * b;
    }

    @Override
    protected float getChromaticRed(float r, float g, float b) {
        return 0.439f * r - 0.399f * g - 0.040f * b;
    }

    @Override
    protected float getR(float y, float cb, float cr) {
        return 1.164f * y + 1.793f * cr;
    }

    @Override
    protected float getG(float y, float cb, float cr) {
        return 1.164f * y - 0.534f * cr - 0.213f * cb;
    }

    @Override
    protected float getB(float y, float cb, float cr) {
        return 1.164f * y + 2.115f * cb;
    }
}
