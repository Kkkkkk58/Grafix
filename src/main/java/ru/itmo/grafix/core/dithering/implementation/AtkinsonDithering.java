package ru.itmo.grafix.core.dithering.implementation;

import ru.itmo.grafix.core.dithering.Dithering;
import ru.itmo.grafix.core.dithering.DitheringType;

public class AtkinsonDithering extends Dithering {
    public AtkinsonDithering() {
        super(DitheringType.ATKINSON);
    }

    @Override
    public float[] convert(float[] data, int width, int height, int bitDepth) {
        return new float[0];
    }
}
