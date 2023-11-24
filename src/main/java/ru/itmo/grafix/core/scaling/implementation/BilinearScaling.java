package ru.itmo.grafix.core.scaling.implementation;

import ru.itmo.grafix.core.scaling.ScalingType;
import ru.itmo.grafix.core.scaling.ScalingWithKernel;

public class BilinearScaling extends ScalingWithKernel {
    public BilinearScaling() {
        super(ScalingType.BILINEAR, 1);
    }

    @Override
    protected float getKernel(double d) {
        double abs = Math.abs(d);
        return (abs > 1) ? 0 : (float) (1 - abs);
    }
}
