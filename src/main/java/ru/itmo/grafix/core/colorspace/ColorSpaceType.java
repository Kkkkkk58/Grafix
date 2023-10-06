package ru.itmo.grafix.core.colorspace;

public enum ColorSpaceType {
    RGB("RGB"),
    CMY("CMY"),
    HSL("HSL"),
    HSV("HSV"),
    YCBCR601("YCbCr.601"),
    YCBCR709("YCbCr.709"),
    YCOCG("YCoCg");


    private final String name;

    ColorSpaceType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
