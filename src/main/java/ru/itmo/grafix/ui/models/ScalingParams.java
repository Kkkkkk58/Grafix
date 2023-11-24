package ru.itmo.grafix.ui.models;

import ru.itmo.grafix.core.scaling.Scaling;

public abstract class ScalingParams {
    private final int width;
    private final int height;
    private final float biasX;
    private final float biasY;
    private final Scaling scalingMethod;

    public ScalingParams(int width, int height, float biasX, float biasY, Scaling scalingMethod) {
        this.width = width;
        this.height = height;
        this.biasX = biasX;
        this.biasY = biasY;
        this.scalingMethod = scalingMethod;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
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
