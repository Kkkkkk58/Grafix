package ru.itmo.grafix.core.dithering.implementation;

import ru.itmo.grafix.core.dithering.Dithering;
import ru.itmo.grafix.core.dithering.DitheringType;

public class OrderedDithering extends Dithering {
    public OrderedDithering() {
        super(DitheringType.ORDERED);
    }
}
