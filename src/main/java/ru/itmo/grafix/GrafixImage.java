package ru.itmo.grafix;

public class GrafixImage {
    private String format;
    private int width;
    private int height;
    private int maxVal;
    private byte[] data;

    public GrafixImage(String format, int width, int height, int maxVal, byte[] data) {
        this.format = format;
        this.width = width;
        this.height = height;
        this.maxVal = maxVal;
        this.data = data;
    }

    public GrafixImage(String format, int width, int height, int maxVal) {
        this(format, width, height, maxVal, new byte[width * height]);
    }

    public String getFormat() {
        return format;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public byte[] getData() {
        return data;
    }
}
