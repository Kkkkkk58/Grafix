package ru.itmo.grafix.core.imageprocessing;

public class GradientGenerator {
    public static byte[] generateGradient(int width, int height) {
        byte[] buffer = new byte[width * height];
        float step = 256f / width;
        float curr = 0f;
        for (int i = 0; i < width; ++i) {
            buffer[i] = (byte) curr;
            curr += step;
        }
        int usedSize = width;
        while (usedSize < height * width) {
            System.arraycopy(buffer, 0, buffer, usedSize, width);
            usedSize += width;
        }
        return buffer;
    }
}
