package ru.itmo.grafix.core.scaling.implementation;

import ru.itmo.grafix.core.scaling.Scaling;
import ru.itmo.grafix.core.scaling.ScalingType;

public class Lanczos3Scaling extends Scaling {
    public Lanczos3Scaling() {
        super(ScalingType.LANCZOS3);
    }
}
