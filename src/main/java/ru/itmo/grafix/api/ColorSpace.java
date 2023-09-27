package ru.itmo.grafix.api;

public abstract class ColorSpace {
    private final ColorSpaceType type;

    protected ColorSpace(ColorSpaceType type) {
        this.type = type;
    }

    public abstract float[] toRGB(float[] buffer);
    public abstract float[] fromRGB(float[] buffer);

    @Override
    public String toString() {
        return type.getName();
    }
}
