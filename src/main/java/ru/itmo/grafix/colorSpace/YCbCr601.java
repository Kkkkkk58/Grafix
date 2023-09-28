package ru.itmo.grafix.colorSpace;

import ru.itmo.grafix.api.ColorSpaceType;

public class YCbCr601 extends YCbCr {
    public YCbCr601() {
        super(ColorSpaceType.YCBCR601, new float[]{0.299f, 0.587f, 0.114f, 1.772f, 1.402f});
    }
}
