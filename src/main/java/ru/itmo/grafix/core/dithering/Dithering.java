package ru.itmo.grafix.core.dithering;

public abstract class Dithering {
    private final DitheringType type;

    protected Dithering(DitheringType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type.getName();
    }
}
