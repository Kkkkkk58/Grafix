package ru.itmo.grafix.api;

public enum ColorSpaceType {
    RGB("RGB"),
    CMY("CMY"),
    HSL("HSL"),
    HSV("HSV");


    private final String name;

    ColorSpaceType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
