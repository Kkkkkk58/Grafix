package ru.itmo.grafix.colorSpace;

import ru.itmo.grafix.api.ColorSpace;
import ru.itmo.grafix.api.ColorSpaceType;

public abstract class YCbCr extends ColorSpace {
    protected YCbCr(ColorSpaceType type) {
        super(type);
    }

    @Override
    public float[] fromRGB(float[] buffer) {
        float[] newBuffer = new float[buffer.length];
        for (int i = 0; i < buffer.length; i += 3) {
            float r = buffer[i], g = buffer[i + 1], b = buffer[i + 2];
            newBuffer[i] = getLuma(r, g, b);
            newBuffer[i + 1] = getChromaticBlue(r, g, b) + 128f / 255f;
            newBuffer[i + 2] = getChromaticRed(r, g, b) + 128f / 255f;
        }

        return newBuffer;
    }

    @Override
    public float[] toRGB(float[] buffer) {
        float[] newBuffer = new float[buffer.length];
        for (int i = 0; i < buffer.length; i += 3) {
            float y = buffer[i], cb = buffer[i + 1] - 128f / 255f, cr = buffer[i + 2] - 128f / 255f;
            newBuffer[i] = getR(y, cb, cr);
            newBuffer[i + 1] = getG(y, cb, cr);
            newBuffer[i + 2] = getB(y, cb, cr);
        }

        return newBuffer;
    }
    @Override
    public float getCoefficient() {
        return 0.5f;
    }

    protected abstract float getLuma(float r, float g, float b);

    protected abstract float getChromaticBlue(float r, float g, float b);

    protected abstract float getChromaticRed(float r, float g, float b);

    protected abstract float getR(float y, float cb, float cr);

    protected abstract float getG(float y, float cb, float cr);

    protected abstract float getB(float y, float cb, float cr);
}
