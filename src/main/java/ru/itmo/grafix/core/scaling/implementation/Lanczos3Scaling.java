package ru.itmo.grafix.core.scaling.implementation;

import ru.itmo.grafix.core.scaling.ScalingType;
import ru.itmo.grafix.core.scaling.ScalingWithKernel;

public class Lanczos3Scaling extends ScalingWithKernel {
    public Lanczos3Scaling() {
        super(ScalingType.LANCZOS3, 3);
    }
    @Override
    protected float getKernel(double d) {
        return (float) (getSinc(d) * getW(d));
    }
    private double getW(double x){
        return Math.abs(x) < 3 ? getSinc(x / 3) : 0;
    }

    private double getSinc(double x){
        return x != 0 ? Math.sin(Math.PI * x) / (Math.PI * x) : 1;
    }
}
