package ru.itmo.grafix.colorSpace;

import ru.itmo.grafix.api.ColorSpace;
import ru.itmo.grafix.api.ColorSpaceType;

public abstract class YCbCr extends ColorSpace {
    private final float[] coefs;

    protected YCbCr(ColorSpaceType type, float[] coefficients) {
        super(type);
        this.coefs = coefficients;
    }

    @Override
    public float[] fromRGB(float[] buffer) {
        float[] newBuffer = new float[buffer.length];
        for (int i = 0; i < buffer.length; i += 3) {
            float r = buffer[i], g = buffer[i + 1], b = buffer[i + 2];
            float y = getLuma(r, g, b);
            newBuffer[i] = y;
            newBuffer[i + 1] = getChromaticBlue(b, y);
            newBuffer[i + 2] = getChromaticRed(r, y);
        }

        return newBuffer;
    }

    @Override
    public float[] toRGB(float[] buffer) {
        float[] newBuffer = new float[buffer.length];
        for (int i = 0; i < buffer.length; i += 3) {
            float y = buffer[i], cb = buffer[i + 1], cr = buffer[i + 2];
            newBuffer[i] = getR(y, cb, cr);
            newBuffer[i + 1] = getG(y, cb, cr);
            newBuffer[i + 2] = getB(y, cb, cr);
        }

        return newBuffer;
    }

    private float getLuma(float r, float g, float b) {
        return coefs[0] * r + coefs[1] * g + coefs[2] * b;
    }

    private float getChromaticBlue(float b, float y) {
        return (b - y) / coefs[3];
    }

    private float getChromaticRed(float r, float y) {
        return (r - y) / coefs[4];
    }

    private float getR(float y, float cb, float cr) {
        return y + coefs[4] * cr;
    }

    private float getG(float y, float cb, float cr) {
        return y - (coefs[0] * coefs[4] / coefs[1]) * cr - (coefs[2] * coefs[3] / coefs[1]) * cb;
    }

    private float getB(float y, float cb, float cr) {
        return y + coefs[3] * cb;
    }
}
