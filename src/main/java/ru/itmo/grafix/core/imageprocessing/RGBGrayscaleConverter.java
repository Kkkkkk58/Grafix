package ru.itmo.grafix.core.imageprocessing;

import ru.itmo.grafix.core.image.GrafixImage;

public class RGBGrayscaleConverter {
    public static GrafixImage convertRGBToGrayscale(GrafixImage image) {
        if (image.isGrayscale()) {
            throw new IllegalArgumentException("Image is already grayscale");
        }

        float[] data = new float[image.getWidth() * image.getHeight()];
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                float sum = 0;
                for (int k = 0; k < image.getTotalChannels(); ++k) {
                    sum += image.getData()[image.getTotalChannels() * (y * image.getWidth() + x) + k];
                }
                data[y * image.getWidth() + x] = sum / image.getTotalChannels();
            }
        }

        return new GrafixImage(
                image.getFormat().replace('6', '5'),
                image.getWidth(),
                image.getHeight(),
                image.getMaxVal(),
                data,
                image.getPath(),
                image.getHeaderSize(),
                image.getColorSpace()
        );
    }

    public static GrafixImage convertGrayscaleToRGB(GrafixImage image) {
        if (!image.isGrayscale()) {
            throw new IllegalArgumentException("Image is not grayscale");
        }

        int bytesPerPixel = 3;
        float[] data = new float[bytesPerPixel * image.getHeight() * image.getWidth()];
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                float value = image.getData()[y * image.getWidth() + x];
                for (int k = 0; k < bytesPerPixel; ++k) {
                    data[bytesPerPixel * (y * image.getWidth() + x) + k] = value;
                }
            }
        }

        return new GrafixImage(
                image.getFormat().replace('5', '6'),
                image.getWidth(),
                image.getHeight(),
                image.getMaxVal(),
                data,
                image.getPath(),
                image.getHeaderSize(),
                image.getColorSpace()
        );
    }
}
