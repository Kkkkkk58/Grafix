package ru.itmo.grafix.core.colorspace.implementation;

import ru.itmo.grafix.core.colorspace.ColorSpace;
import ru.itmo.grafix.core.colorspace.ColorSpaceType;

public class YCoCg extends ColorSpace {
    public YCoCg() {
        super(ColorSpaceType.YCOCG);
    }

    @Override
    public float[] toRGB(float[] buffer) {
        float[] newBuffer = new float[buffer.length];
        for (int i = 0; i < buffer.length; i += 3) {
            float y = buffer[i], co = buffer[i + 1], cg = buffer[i + 2];
            float tmp = y - cg;
            newBuffer[i] = tmp + co;
            newBuffer[i + 1] = y + cg;
            newBuffer[i + 2] = tmp - co;
        }

        return newBuffer;
    }

    @Override
    public float[] fromRGB(float[] buffer) {
        float[] newBuffer = new float[buffer.length];
        for (int i = 0; i < buffer.length; i += 3) {
            float r = buffer[i], g = buffer[i + 1], b = buffer[i + 2];
            newBuffer[i] = getLuma(r, g, b);
            newBuffer[i + 1] = getChrominanceOrange(r, g, b);
            newBuffer[i + 2] = getChrominanceGreen(r, g, b);
        }

        return newBuffer;
    }

    @Override
    public int[] getAutocorrectionChannels() {
        return new int[]{0};
    }

    @Override
    public float getCoefficient() {
        return 0.0f;
    }

    @Override
    public float normalize(float value) {
        return ((byte) (value * 255) & 0xff) / 255f;
    }

    private float getLuma(float r, float g, float b) {
        return 0.25f * r + 0.5f * g + 0.25f * b;
    }

    private float getChrominanceOrange(float r, float g, float b) {
        return 0.5f * r - 0.5f * b;
    }

    private float getChrominanceGreen(float r, float g, float b) {
        return -0.25f * r + 0.5f * g - 0.25f * b;
    }
}
