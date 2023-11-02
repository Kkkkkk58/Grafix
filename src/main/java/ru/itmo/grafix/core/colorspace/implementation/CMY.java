package ru.itmo.grafix.core.colorspace.implementation;

import ru.itmo.grafix.core.colorspace.ColorSpace;
import ru.itmo.grafix.core.colorspace.ColorSpaceType;
import ru.itmo.grafix.core.image.GrafixImage;

public class CMY extends ColorSpace {
    public CMY() {
        super(ColorSpaceType.CMY);
    }

    @Override
    public float[] toRGB(float[] buffer) {
        float[] newBuffer = new float[buffer.length];
        for (int i = 0; i < buffer.length; i += 3) {
            newBuffer[i] = 1 - buffer[i];
            newBuffer[i + 1] = 1 - buffer[i + 1];
            newBuffer[i + 2] = 1 - buffer[i + 2];
        }
        return newBuffer;
    }

    @Override
    public float[] fromRGB(float[] buffer) {
        return toRGB(buffer);
    }

    @Override
    public int[] getAutocorrectionChannels() {
        return new int[]{0, 1, 2};
    }

    @Override
    public float[] applyAutocorrection(GrafixImage image, double ignoreCoefficient) {
        return new float[0];
    }

    @Override
    public float getCoefficient() {
        return 0;
    }
}
