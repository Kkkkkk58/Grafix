package ru.itmo.grafix.api;

public enum ColorSpaceType {
    RGB("RGB"),
    HSL("HSL"),
    HSV("HSV"),
    CMY("CMY");


    private final String name;

    ColorSpaceType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
