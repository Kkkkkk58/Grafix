package ru.itmo.grafix.core.scaling;

import ru.itmo.grafix.core.image.GrafixImage;

public abstract class Scaling {

    private final ScalingType type;

    protected Scaling(ScalingType type) {
        this.type = type;
    }

    public abstract GrafixImage applyScaling(GrafixImage oldImage, int width, int height, float biasX, float biasY);

    public ScalingType getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return type.getName();
    }

    protected int getLinearCoordinate(int x, int y, int k, int width, int height, int bytesPerPixel) {
        x = Math.min(Math.max(x, 0), width - 1);
        y = Math.min(Math.max(y, 0), height - 1);
        return bytesPerPixel * (y * width + x) + k;
    }
}