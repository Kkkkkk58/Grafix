package ru.itmo.grafix.colorSpace;

import ru.itmo.grafix.api.ColorSpaceType;

public class YCbCr709 extends YCbCr {
    public YCbCr709() {
        super(ColorSpaceType.YCBCR709, new float[]{0.2126f, 0.7152f, 0.0722f, 1.8556f, 1.5748f});
    }
}
