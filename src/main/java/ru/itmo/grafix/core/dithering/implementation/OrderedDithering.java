package ru.itmo.grafix.core.dithering.implementation;

import ru.itmo.grafix.core.dithering.Dithering;
import ru.itmo.grafix.core.dithering.DitheringType;

public class OrderedDithering extends Dithering {

    byte[] bayerMatrix = new byte[64];
    public OrderedDithering() {
        super(DitheringType.ORDERED);
        float multiplier = 1f / 64f;
//        for(int i = 0;)
    }

    @Override
    public byte[] convert(byte[] data, int width, int height, int bitDepth) {
        return new byte[0];
    }
}
