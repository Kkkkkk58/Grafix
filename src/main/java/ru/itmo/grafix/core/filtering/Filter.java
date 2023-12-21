package ru.itmo.grafix.core.filtering;

import ru.itmo.grafix.core.image.GrafixImage;

public abstract class Filter {

    private final FilterType type;

    protected Filter(FilterType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type.getName();
    }

    public abstract boolean setParams();

    public abstract GrafixImage apply(GrafixImage image);
}
