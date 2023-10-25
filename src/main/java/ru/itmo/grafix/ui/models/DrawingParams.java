package ru.itmo.grafix.ui.models;

public class DrawingParams {
    private final float thickness;
    private final float[] color;
    private final float opacity;

    public DrawingParams(float thickness, float[] color, float opacity) {
        this.thickness = thickness;
        this.color = color;
        this.opacity = opacity;
    }

    public float getThickness() {
        return thickness;
    }

    public float[] getColor() {
        return color;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setColor(byte[] color) {
        this.color = color;
    }

}
