package ru.itmo.grafix.core.dithering.implementation;

import ru.itmo.grafix.core.dithering.Dithering;
import ru.itmo.grafix.core.dithering.DitheringType;

import java.util.function.BiFunction;

public class FloydSteinbergDithering extends Dithering {
    int[][] errorRowColumnAdjustments = new int[][]{{0, 1}, {1, -1}, {1, 0}, {1, 1}};
    float[] factors = new float[]{7 / 16f, 3 / 16f, 5 / 16f, 1 / 16f};

    public FloydSteinbergDithering() {
        super(DitheringType.FLOYD_STEINBERG);
    }

    @Override
    public float[] convert(float[] data, int width, int height, int bitDepth, float gamma) {
        float[] buffer = new float[data.length];
        int bytesPerPixel = data.length / (width * height);
        BiFunction<Integer, Integer, Integer> getRowColumnIndex = (i, j) -> bytesPerPixel * (i * width + j);
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                for (int k = 0; k < bytesPerPixel; ++k) {
                    int index = getRowColumnIndex.apply(i, j) + k;
                    float oldPixel = data[index] + buffer[index];
                    float newPixel = getNearestPaletteColor(oldPixel, bitDepth, gamma, 0.5f);
                    buffer[index] = newPixel;
                    for (int factorInd = 0; factorInd < factors.length; ++factorInd) {
                        int[] diDj = errorRowColumnAdjustments[factorInd];
                        float errFactor = (oldPixel - newPixel) * factors[factorInd];
                        int di = diDj[0];
                        int dj = diDj[1];
                        int iNew = i + di;
                        int jNew = j + dj;
                        if (iNew > -1 && iNew < height && jNew < width && jNew > -1) {
                            buffer[getRowColumnIndex.apply(iNew, jNew) + k] += errFactor;
                        }
                    }
                }
            }
        }

        return buffer;
    }
}
