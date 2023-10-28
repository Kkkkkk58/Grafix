package ru.itmo.grafix.core.dithering;

public enum DitheringType {
    ATKINSON("Atkinson"),
    FLOYD_STEINBERG("Floyd-Steinberg"),
    ORDERED("Ordered"),
    RANDOM("Random");


    private final String name;

    DitheringType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
