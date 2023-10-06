package ru.itmo.grafix.core.imageprocessing;

import java.util.Arrays;

public class GammaCorrecter {

    private static final float gammaConst = 0.0031308f;

    public static float[] convertGamma(float gamma, float previousGamma, float[] data) {
        float[] dataBuffer = Arrays.copyOf(data, data.length);
        for (int i = 0; i < data.length; ++i) {
            double buffer = data[i];
            if (previousGamma != 0) {
                buffer = Math.pow(buffer, 1.0 / previousGamma);
            }
            dataBuffer[i] = getConvertedValue(gamma, buffer);

        }
        return dataBuffer;
    }

    private static float getConvertedValue(float gamma, double buffer) {
        if (gamma != 0) {
            return (float) Math.pow(buffer, gamma);
        } else if (buffer < gammaConst) {
            return (float) (buffer * 12.92f);
        }
//      (float) Math.pow((buffer + 0.055f) / 1.055f, 2.4f); - inverse
        return 1.055f * (float) Math.pow(buffer, 1.0 / 2.4) - 0.055f;
    }
}
