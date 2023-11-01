package ru.itmo.grafix.core.autocorrection;

public class ImageHistogramData {
    private final int[] data;

    public ImageHistogramData(int[] data) {
        this.data = data;
    }

    public int[] getData() {
        return data;
    }
}
