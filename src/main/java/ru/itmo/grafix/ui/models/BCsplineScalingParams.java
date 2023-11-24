package ru.itmo.grafix.ui.models;

import ru.itmo.grafix.core.scaling.Scaling;

public class BCsplineScalingParams extends ScalingParams{

    private final float B;
    private final float C;
    public BCsplineScalingParams(int width, int height, float biasX, float biasY, Scaling scalingMethod, float b, float c) {
        super(width, height, biasX, biasY, scalingMethod);
        B = b;
        C = c;
    }

    public float getB() {
        return B;
    }

    public float getC() {
        return C;
    }
}
