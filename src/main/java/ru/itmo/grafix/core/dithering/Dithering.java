package ru.itmo.grafix.core.dithering;

public abstract class Dithering {
    private final DitheringType type;

    protected Dithering(DitheringType type) {
        this.type = type;
    }

    public abstract byte[] convert(byte[] data, int width, int height);

    @Override
    public String toString() {
        return type.getName();
    }
}
