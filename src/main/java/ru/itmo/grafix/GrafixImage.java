package ru.itmo.grafix;

public class GrafixImage {
    private String format;
    private int width;
    private int height;
    private int maxVal;
    private float[] data;
    private String path;
    private int headerSize;

    public GrafixImage(String format, int width, int height, int maxVal, float[] data, String path, int headerSize) {
        this.format = format;
        this.width = width;
        this.height = height;
        this.maxVal = maxVal;
        this.data = data;
        this.path = path;
        this.headerSize = headerSize;
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

    public float[] getData() {
        return data;
    }
    public String getPath() {
        return path;
    }
    public int getHeaderSize(){return headerSize;}
    public int getMaxVal(){return maxVal;}
}
