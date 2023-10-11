package ru.itmo.grafix.core.dithering.implementation;

import ru.itmo.grafix.core.dithering.Dithering;
import ru.itmo.grafix.core.dithering.DitheringType;

import java.util.Arrays;
import java.util.function.BiFunction;

public class AtkinsonDithering extends Dithering {
    int[][] errorRowColumnAdjustments = new int[][] {{0, 1}, {0, 2}, {1, -1}, {1, 1}, {2, 0}};
    public AtkinsonDithering() {
        super(DitheringType.ATKINSON);
    }

    @Override
    public float[] convert(float[] data, int width, int height, int bitDepth) {
        float[] buffer = Arrays.copyOf(data, data.length);
        int bytesPerPixel = data.length / (width * height);
        BiFunction<Integer, Integer, Integer> getRowColumnIndex = (i, j) -> bytesPerPixel * (i * width + j);
        float factor = 1 / 8f;
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                for (int k = 0; k < bytesPerPixel; ++k) {
                    int index = getRowColumnIndex.apply(i, j) + k;
                    float oldPixel = buffer[index];
                    float newPixel = getNearestPaletteColor(oldPixel, bitDepth);
                    buffer[index] = newPixel;
                    float errFactor = (oldPixel - newPixel) * factor;
                    for (int[] diDj : errorRowColumnAdjustments) {
                        int di = diDj[0];
                        int dj = diDj[1];
                        int iNew = i + di;
                        int jNew = j + dj;
                        if (iNew < height && jNew < width && jNew > -1) {
                            buffer[getRowColumnIndex.apply(iNew, jNew) + k] += errFactor;
                        }
                    }
                }
            }
        }

        return buffer;
    }
}
