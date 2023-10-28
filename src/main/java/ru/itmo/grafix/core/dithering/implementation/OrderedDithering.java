package ru.itmo.grafix.core.dithering.implementation;

import ru.itmo.grafix.core.dithering.Dithering;
import ru.itmo.grafix.core.dithering.DitheringType;

public class OrderedDithering extends Dithering {

    private static final float[][] MATRIX = new float[][]{
            {0, 0.5f, 8f/64f, 40f/64f, 2f/64f, 34f/64f, 10f/64f, 42f/64f},
            {48f/64f, 0.25f, 56f/64f, 24f/64f, 50f/64f, 18f/64f, 58f/64f, 26f/64f},
            {12f/64f, 44f/64f, 4f/64f, 36f/64f, 14f/64f, 46f/64f, 6f/64f, 38f/64f},
            {60f/64f, 28f/64f, 52f/64f, 20f/64f, 62f/64f, 30f/64f, 54f/64f, 22f/64f},
            {3f/64f, 35f/64f, 11f/64f, 43f/64f, 1f/64f, 33f/63f, 9f/64f, 41f/64f},
            {51f/64f, 19f/64f, 59f/64f, 27f/64f, 49f/64f, 17f/64f, 57f/64f, 25f/64f},
            {15f/64f, 47f/64f, 7f/64f, 39f/64f, 13f/64f, 45f/64f, 5f/64f, 37f/64f},
            {63f/64f, 31f/64f, 55f/64f, 23f/64f, 61f/64f, 29f/64f, 53f/64f, 21f/64f}
    };

    public OrderedDithering() {
        super(DitheringType.ORDERED);
    }

    @Override
    public float[] convert(float[] data, int width, int height, int bitDepth, float gamma) {
        float[] buffer = new float[data.length];
        int bytesPerPixel = data.length / (width * height);
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width * bytesPerPixel; j += bytesPerPixel) {
                for (int k = 0; k < bytesPerPixel; ++k) {
                    int index = i * width * bytesPerPixel + j + k;
                    float value = data[index] + (MATRIX[i % MATRIX.length][j % MATRIX.length] - 0.5f) / bitDepth;
                    buffer[index] = getNearestPaletteColor(data[index], bitDepth, gamma, MATRIX[i % MATRIX.length][j % MATRIX.length]);
                }
            }
        }

        return buffer;
    }
}
