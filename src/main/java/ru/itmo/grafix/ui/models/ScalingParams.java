package ru.itmo.grafix.ui.models;

import ru.itmo.grafix.core.scaling.Scaling;

public abstract class ScalingParams {
    private final float width;
    private final float height;
    private final float biasX;
    private final float biasY;
    private final Scaling scalingMethod;

    public ScalingParams(float width, float height, float biasX, float biasY, Scaling scalingMethod) {
        this.width = width;
        this.height = height;
        this.biasX = biasX;
        this.biasY = biasY;
        this.scalingMethod = scalingMethod;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getBiasX() {
        return biasX;
    }

    public float getBiasY() {
        return biasY;
    }

    public Scaling getScalingMethod() {
        return scalingMethod;
    }
}
