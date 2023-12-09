package ru.itmo.grafix.core.filtering;

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
}
