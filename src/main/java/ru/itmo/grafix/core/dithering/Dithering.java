package ru.itmo.grafix.core.dithering;

public abstract class Dithering {
    private final DitheringType type;

    protected Dithering(DitheringType type) {
        this.type = type;
    }

    protected float getNearestPaletteColor(float value, int bitDepth) {
        return Math.round(value * bitDepth) / (float) bitDepth;
    }

    public abstract float[] convert(float[] data, int width, int height, int bitDepth);

    @Override
    public String toString() {
        return type.getName();
    }
}
