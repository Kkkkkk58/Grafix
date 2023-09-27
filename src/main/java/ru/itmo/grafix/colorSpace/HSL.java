package ru.itmo.grafix.colorSpace;

import ru.itmo.grafix.api.ColorSpace;
import ru.itmo.grafix.api.ColorSpaceType;

public class HSL extends ColorSpace {
    public HSL() {
        super(ColorSpaceType.HSL);
    }

    @Override
    public float[] toRGB(float[] buffer) {
        float[] newBuffer = new float[buffer.length];
        for (int i = 0; i < buffer.length; i += 3) {
            float H = buffer[i];
            float S = buffer[i + 1];
            float L = buffer[i + 2];
            float H_ = H * 6f;
            float C = (1 - Math.abs(2f * L - 1)) * S;
            float X = C * (1 - Math.abs(H_ % 2 - 1));
            float m = L - C / 2f;
            float[] RGB = getRGB(H_, m, C, X);
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
            float H = getH(buffer[i], buffer[i + 1], buffer[i + 2], C, V);
            float S = 0;
            if(L != 1 && L != 0){
                S = (V - L) / Math.min(L, 1-L);
            }
            newBuffer[i] = H;
            newBuffer[i + 1] = S;
            newBuffer[i + 2] = L;
        }
        return newBuffer;
    }

    public static float getH(float R, float G, float B, float C, float V) {
        if (C == 0) {
            return 0;
        }
        if (V == R) {
            return (((G - B) / C) % 6) * 60f / 360f;
        }
        if (V == G) {
            return (((B - R) / C) + 2f) * 60f / 360f;
        }
        if (V == B) {
            return (((R - G) / C) + 4f) * 60f / 360f;
        }
        //Add throwing exception
        return 0;
    }

    public static float[] getRGB(float H_, float m, float C, float X){
        float R = m;
        float G = m;
        float B = m;
        if ((0 <= H_) && (H_ < 1)){
            R += C;
            G += X;
        }else if ((1 <= H_) && (H_ < 2)){
            R += X;
            G += C;
        }else if ((2 <= H_) && (H_ < 3)){
            B += X;
            G += C;
        }else if ((3 <= H_) && (H_ < 4)){
            B += C;
            G += X;
        }else if ((4 <= H_) && (H_ < 5)){
            R += X;
            B += C;
        }else if ((5 <= H_) && (H_ < 6)){
            R += C;
            B += X;
        }
        return new float[]{R, G, B};
    }
}