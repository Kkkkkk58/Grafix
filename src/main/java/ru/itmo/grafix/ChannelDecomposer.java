package ru.itmo.grafix;

import ru.itmo.grafix.api.ColorSpace;

import java.util.Objects;

public class ChannelDecomposer {
    public static float[] decompose(float[] data, int channelNumber, ColorSpace space) {
        int ch1 = 1;
        int ch2 = 2;
        switch (channelNumber) {
            case 2 -> ch1 = 0;
            case 3 -> {
                ch1 = 0;
                ch2 = 1;
            }
        }
        float coeff = space.getCoefficient();
        //TODO: think about it
        if (channelNumber != 1 && (Objects.equals(space.toString(), "HSL") || Objects.equals(space.toString(), "HSV"))) {
            coeff = 0.0f;
        } else if (Objects.equals(space.toString(), "YCoCg") && channelNumber != 1) {
            coeff = 0.5f;
        }
        channelNumber -= 1;
        float[] newData = new float[data.length];
        for (int i = 0; i < data.length; i += 3) {
            newData[i + channelNumber] = data[i + channelNumber];
            newData[i + ch1] = coeff;
            newData[i + ch2] = space.getCoefficient();
        }
        return newData;
    }

    public static GrafixImage getDecomposedImage(GrafixImage image) {
        if (Objects.equals(image.getChannel(), "all")) {
            return image;
        }
        float[] newData = new float[image.getData().length / 3];
        int channel = Integer.parseInt(image.getChannel()) - 1;
        for (int i = 0; i < newData.length; ++i) {
            newData[i] = image.getData()[i * 3 + channel];
        }
        return new GrafixImage("P5", image.getWidth(), image.getHeight(), image.getMaxVal(), newData, image.getPath(),
                image.getHeaderSize(), image.getColorSpace());
    }
}
