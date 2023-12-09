package ru.itmo.grafix.core.filtering;

public enum FilterType {
    LINEAR("Linear averaging");
    private final String name;

    FilterType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
