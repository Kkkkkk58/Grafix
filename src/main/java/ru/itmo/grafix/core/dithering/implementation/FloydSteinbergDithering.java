package ru.itmo.grafix.core.dithering.implementation;

import ru.itmo.grafix.core.dithering.Dithering;
import ru.itmo.grafix.core.dithering.DitheringType;

public class FloydSteinbergDithering extends Dithering {
    public FloydSteinbergDithering() {
        super(DitheringType.FLOYD_STEINBERG);
    }
}
