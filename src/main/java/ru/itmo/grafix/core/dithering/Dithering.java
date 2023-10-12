package ru.itmo.grafix.core.dithering;

import ru.itmo.grafix.core.imageprocessing.GammaCorrecter;

public abstract class Dithering {
    private final DitheringType type;

    protected Dithering(DitheringType type) {
        this.type = type;
    }

    protected float getNearestPaletteColor(float value, int bitDepth, float gamma, float threshold) {
//        return Math.round(value * bitDepth) / (float) bitDepth;
        if(value == 1.0f){
            return value;
        }
        value *= 255;
        float lower = getLower(value, bitDepth, gamma);
        float upper = getUpper(value, bitDepth, gamma);
        float l = GammaCorrecter.getConvertedValue(gamma, lower / 255f);
        float u = GammaCorrecter.getConvertedValue(gamma, upper / 255f);
        float convertedValue = GammaCorrecter.getConvertedValue(gamma, value / 255f);
        return (convertedValue - l > threshold * (u - l)) ? Math.min(u, 1.0f) : Math.max(l, 0.0f);
    }

    public abstract float[] convert(float[] data, int width, int height, int bitDepth, float gamma);

    @Override
    public String toString() {
        return type.getName();
    }

    private int getLower(float value, int bitDepth, float gamma){
       return (int) (Math.floor(value / (255f / (Math.pow(2, bitDepth) - 1))) * (255f / (Math.pow(2, bitDepth) - 1)));
    }
    private int getUpper(float value, int bitDepth, float gamma){
        return (int) (Math.ceil(value / (255f / (Math.pow(2, bitDepth) - 1))) * (255f / (Math.pow(2, bitDepth) - 1)));
    }
}
