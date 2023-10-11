package ru.itmo.grafix.core.dithering.implementation;

import ru.itmo.grafix.core.dithering.Dithering;
import ru.itmo.grafix.core.dithering.DitheringType;

import java.util.concurrent.ThreadLocalRandom;

public class RandomDithering extends Dithering {
    private final ThreadLocalRandom rand;
    public RandomDithering() {
        super(DitheringType.RANDOM);
        this.rand = ThreadLocalRandom.current();
    }

    @Override
    public float[] convert(float[] data, int width, int height, int bitDepth) {
        float[] buffer = new float[data.length];

        for (int i = 0; i < data.length; ++i) {
            float value = (float) (data[i] + (rand.nextFloat() - 0.5) / bitDepth);
            buffer[i] = getNearestPaletteColor(value, bitDepth);
        }

        return buffer;
    }
}
