package ru.itmo.grafix.core.scaling.implementation;

import ru.itmo.grafix.core.scaling.ScalingType;
import ru.itmo.grafix.core.scaling.ScalingWithKernel;

public class BCsplineScaling extends ScalingWithKernel {

    private float B = 0;
    private float C = 0.5f;
    public BCsplineScaling() {
        super(ScalingType.BC_SPLINE, 2);
    }
    public void setB(float b){
        B = b;
    }

    public void setC(float c){
        C = c;
    }

    @Override
    protected float getKernel(double d) {
        if (Math.abs(d) < 1) {
            return (float) (1f / 6 * ((12 - 9 * B - 6 * C) * Math.pow(Math.abs(d), 3) + (-18 + 12 * B + 6 * C) * Math.pow(Math.abs(d), 2) + 6 - 2 * B));
        }
        if (Math.abs(d) < 2) {
            return (float) (1f / 6 * ((-B - 6 * C) * Math.pow(Math.abs(d), 3) + (6 * B + 30 * C) * Math.pow(Math.abs(d), 2) + (-12 * B - 48 * C) * Math.abs(d) + 8 * B + 24 * C));
        }
        return 0;
    }
}
