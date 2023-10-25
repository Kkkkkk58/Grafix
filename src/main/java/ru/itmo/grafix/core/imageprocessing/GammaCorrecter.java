package ru.itmo.grafix.core.imageprocessing;

import java.util.Arrays;

public class GammaCorrecter {

    private static final float gammaConst = 0.0031308f;
    private static final float gammaReversedConst = 0.04045f;

    public static float[] convertGamma(float gamma, float previousGamma, float[] data) {
        float[] dataBuffer = Arrays.copyOf(data, data.length);
        for (int i = 0; i < data.length; ++i) {
            float buffer = getReversedGamma(data[i], previousGamma);
            dataBuffer[i] = getConvertedValue(gamma, buffer);

        }
        return dataBuffer;
    }

    public static float[] restoreGamma(float previousGamma, float[] data) {
        float[] dataBuffer = Arrays.copyOf(data, data.length);
        if (previousGamma == 1) {
            return dataBuffer;
        }
        for (int i = 0; i < data.length; ++i) {
            dataBuffer[i] = getReversedGamma(data[i], previousGamma);
        }
        return dataBuffer;
    }

    public static float getConvertedValue(float gamma, float buffer) {
        if (gamma != 0) {
            return (float) Math.pow(buffer, gamma);
        } else if (buffer < gammaConst) {
            return (float) (buffer * 12.92f);
        }
        return 1.055f * (float) Math.pow(buffer, 1.0 / 2.4) - 0.055f;
    }

    public static float getReversedGamma(float data, float gamma) {
        if (gamma == 1) {
            return data;
        } else if (gamma != 0) {
            return (float) Math.pow(data, 1.0 / gamma);
        } else {
            if (data <= gammaReversedConst) {
                return data / 12.92f;
            } else {
                return (float) Math.pow((data + 0.055f) / 1.055f, 2.4f);
            }
        }
    }
}
