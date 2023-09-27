package ru.itmo.grafix;

public class FbConverter {
    public static float[] convertBytesToFloat(byte[] buffer, int maxValue){
        float[] newBuffer = new float[buffer.length];
        for (int x = 0; x < buffer.length; x++){
            newBuffer[x] = buffer[x]/ (float)maxValue;
        }
        return newBuffer;
    }

    public static byte[] convertFloatToByte(float[] buffer){
        byte[] newBuffer = new byte[buffer.length];
        for (int x = 0; x < buffer.length; x++){
            newBuffer[x] = (byte) (buffer[x] * 255);
        }
        return newBuffer;
    }
}