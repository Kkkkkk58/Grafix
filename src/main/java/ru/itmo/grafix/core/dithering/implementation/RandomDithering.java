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
        int step = data.length / (width * height);

        for (int i = 0; i < data.length; i += step) {
            for (int k = 0; k < step; ++k) {
                float threshold = rand.nextFloat();
                float value = (float) (data[i + k] + (threshold - 0.5) / bitDepth);
                buffer[i + k] = getNearestPaletteColor(value, bitDepth);
            }
        }

        return buffer;
    }
}
