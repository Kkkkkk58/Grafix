package ru.itmo.grafix.core.dithering.implementation;

import ru.itmo.grafix.core.dithering.Dithering;
import ru.itmo.grafix.core.dithering.DitheringType;

public class RandomDithering extends Dithering {
    public RandomDithering() {
        super(DitheringType.RANDOM);
    }
}
