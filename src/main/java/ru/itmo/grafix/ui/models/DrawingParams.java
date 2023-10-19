package ru.itmo.grafix.ui.models;

public class DrawingParams {
    private float thickness;

    private byte[] color;
    private float opacity;
    public DrawingParams(float thickness, byte[] color, float opacity) {
        this.thickness = thickness;
        this.color = color;
        this.opacity = opacity;
    }

    public float getThickness() {
        return thickness;
    }

    public byte[] getColor() {
        return color;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setColor(byte[] color) {
        this.color = color;
    }

}
