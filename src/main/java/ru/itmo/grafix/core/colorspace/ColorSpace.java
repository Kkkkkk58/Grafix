package ru.itmo.grafix.core.colorspace;

import ru.itmo.grafix.core.imageprocessing.PixelValueNormalizer;

import java.util.Objects;

public abstract class ColorSpace {
    private final ColorSpaceType type;


    protected ColorSpace(ColorSpaceType type) {
        this.type = type;
    }

    public abstract float[] toRGB(float[] buffer);

    public abstract float[] fromRGB(float[] buffer);

    public abstract int[] getAutocorrectionChannels();

    public abstract float getCoefficient();

    public int getIndex() {
        return type.ordinal();
    }

    public float normalize(float value) {
        return PixelValueNormalizer.normalize(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColorSpace that = (ColorSpace) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        return type.getName();
    }
}
