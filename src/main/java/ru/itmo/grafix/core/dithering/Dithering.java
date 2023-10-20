package ru.itmo.grafix.core.dithering;

import ru.itmo.grafix.core.imageprocessing.GammaCorrecter;

public abstract class Dithering {
    private final DitheringType type;

    protected Dithering(DitheringType type) {
        this.type = type;
    }

    protected float getNearestPaletteColor(float value, int bitDepth, float gamma, float threshold, boolean flag) {
//        return Math.round(value * bitDepth) / (float) bitDepth;
//        if(value <= 0.0f){
//            return 0;
//        }
//        if(value >= 1.0f){
//            return value;
//        }
        float v = GammaCorrecter.getConvertedValue(gamma, value);
        float lower = getLower(v * 255f, bitDepth, gamma) / 255f;
        float upper = getUpper(v * 255f, bitDepth, gamma) / 255f;
        float l = GammaCorrecter.getReversedGamma(lower, gamma);
        float u = GammaCorrecter.getReversedGamma(upper, gamma);
        float convertedValue = GammaCorrecter.getConvertedValue(gamma, value / 255f);
//        if(flag) {
//            threshold = GammaCorrecter.getConvertedValue(gamma, threshold);
//            return (convertedValue - l > threshold * (u - l)) ? Math.min(u, 1.0f) : Math.max(l, 0.0f);
//        }
//        else{
//            return (convertedValue - l > threshold * (u - l)) ? Math.min(upper / 255f, 1.0f) : Math.max(lower / 255f, 0.0f);
//        }
        return (value - l > threshold * (u - l)) ? upper : lower;//Math.min(upper, 1.0f) : Math.max(lower, 0.0f);
//        return (convertedValue - l > threshold * (u - l)) ? Math.min(upper / 255f, 1.0f) : Math.max(lower / 255f, 0.0f);
    }

    public DitheringType getType() {
        return type;
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
