package ru.itmo.grafix.core.imageprocessing;

public class IHDR {
    private int width;
    private int height;
    private int depth;
    private int colorType;
    private boolean compression;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return depth;
    }

    public int getColorType() {
        return colorType;
    }

    public boolean isCompression() {
        return compression;
    }

    public IHDR(int width, int height, int depth, int colorType, boolean compression) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.colorType = colorType;
        this.compression = compression;
    }
}
