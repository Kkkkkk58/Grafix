package ru.itmo.grafix.core.imageprocessing;

import java.util.Arrays;

public class GammaCorrecter {

    private static final float gammaConst = 0.0031308f;

    public static float[] convertGamma(Float gamma, float previousGamma, float[] data) {
        float[] dataBuffer = Arrays.copyOf(data, data.length);
        for (int i = 0; i < data.length; ++i) {
            double buffer = data[i];
            if (previousGamma != 0) {
                buffer = Math.pow(buffer, 1.0 / previousGamma);
            }
            if (gamma != 0) {
                dataBuffer[i] = (float) Math.pow(buffer, gamma);
            } else if (buffer < gammaConst) {
                dataBuffer[i] = (float) (buffer * 12.92f);
            } else {
                dataBuffer[i] = 1.055f * (float) Math.pow(buffer, 1.0 / 2.4) - 0.055f;
//                data[i] = (float) Math.pow((buffer + 0.055f) / 1.055f, 2.4f);
            }
        }
        return dataBuffer;
    }
}
