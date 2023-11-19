package ru.itmo.grafix.ui.models;

import ru.itmo.grafix.core.scaling.Scaling;

public class SimpleScalingParams extends ScalingParams{

    public SimpleScalingParams(float width, float height, float biasX, float biasY, Scaling scalingMethod) {
        super(width, height, biasX, biasY, scalingMethod);
    }
}
