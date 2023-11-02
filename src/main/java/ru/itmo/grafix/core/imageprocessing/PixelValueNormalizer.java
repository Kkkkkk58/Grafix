package ru.itmo.grafix.core.imageprocessing;

public class PixelValueNormalizer {
    public static float normalize(float value) {
        if (value < 0) {
            return 0;
        }
        if (value > 1) {
            return 1;
        }
        return value;
    }
}
