package ru.itmo.grafix.colorSpace;

import ru.itmo.grafix.api.ColorSpace;
import ru.itmo.grafix.api.ColorSpaceType;

public class HSV extends ColorSpace {
    public HSV() {
        super(ColorSpaceType.HSV);
    }

    @Override
    public float[] toRGB(float[] buffer) {
        float[] newBuffer = new float[buffer.length];
        for (int i = 0; i < buffer.length; i += 3) {
            float H = buffer[i];
            float S = buffer[i + 1];
            float V = buffer[i + 2];
            float H_ = H * 6f;
            float C = V * S;
            float X = C * (1 - Math.abs(H_ % 2 - 1));
            float m = V - C;
            float[] RGB = HSL.getRGB(H_, m, C, X);
            newBuffer[i] = RGB[0];
            newBuffer[i + 1] = RGB[1];
            newBuffer[i + 2] = RGB[2];
        }
        return newBuffer;
    }

    @Override
    public float[] fromRGB(float[] buffer) {
        float[] newBuffer = new float[buffer.length];
        for (int i = 0; i < buffer.length; i += 3) {
            float V = Math.max(Math.max(buffer[i], buffer[i + 1]), buffer[i + 2]);
            float minVal = Math.min(Math.min(buffer[i], buffer[i + 1]), buffer[i + 2]);
            float C = V - minVal;
            float L = newBuffer[i + 2] = V - C / 2f;
            float H = HSL.getH(buffer[i], buffer[i + 1], buffer[i + 2], C, V);
            float S = 0;
            if(L != 1 && L != 0){
                S = C / V;
            }
            newBuffer[i] = H;
            newBuffer[i + 1] = S;
            newBuffer[i + 2] = V;
        }
        return newBuffer;
    }

    @Override
    public float getCoefficient() {
        return 0.5f;
    }
}
