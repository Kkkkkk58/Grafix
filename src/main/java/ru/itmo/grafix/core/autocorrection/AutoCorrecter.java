package ru.itmo.grafix.core.autocorrection;

import ru.itmo.grafix.core.image.GrafixImage;
import ru.itmo.grafix.core.imageprocessing.ChannelDecomposer;
import ru.itmo.grafix.core.imageprocessing.PixelValueNormalizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AutoCorrecter {

    public static GrafixImage applyAutocorrection(GrafixImage image, double ignoreCoefficient) {
        if (image.getChannel() != 0) {
            image = ChannelDecomposer.getDecomposedImage(image);
        }
        float[] buffer = image.getData();

        int[] channels = image.getColorSpace().getAutocorrectionChannels();
        if (Objects.equals(image.getFormat(), "P5")) {
            channels = new int[]{0};
        }
        List<ImageHistogramData> neededHistogramData = new ArrayList<ImageHistogramData>(channels.length);
        List<ImageHistogramData> histogramData = ImageHistogramExtractor.getImageHistogramData(image);
        for (int channel : channels) {
            neededHistogramData.add(histogramData.get(channel));
        }
        int skipPixelsCount = (int) (ignoreCoefficient * (buffer.length / image.getTotalChannels()));
        int[] bounds = AutoCorrecter.getBounds(neededHistogramData, skipPixelsCount);
        float[] newBuffer = Arrays.copyOf(buffer, buffer.length);
        int index = 0;
        if (image.getChannel() != 0) {
            index = image.getChannel() - 1;
        }

        for (int i = 0; i < buffer.length / image.getTotalChannels(); ++i) {
            for (int j = 0; j < channels.length; ++j) {
                int newIndex = i * image.getTotalChannels() + index + channels[j];
                newBuffer[newIndex] = AutoCorrecter.convertBrightness(bounds[0], bounds[1], buffer[newIndex] * 255f) / 255f;
            }
        }
        newBuffer = image.getColorSpace().toRGB(newBuffer);
        for (int i = 0; i < buffer.length; ++i) {
            newBuffer[i] = PixelValueNormalizer.normalize(newBuffer[i]);
        }
        image.setData(newBuffer);
        return image;
    }

    public static int[] getBounds(List<ImageHistogramData> histogramData, int skipPixelsCount) {
        int[] bounds = AutoCorrecter.getMinMax(histogramData.get(0), skipPixelsCount);
        for (int i = 1; i < histogramData.size(); ++i) {
            int[] newBounds = AutoCorrecter.getMinMax(histogramData.get(i), skipPixelsCount);
            bounds[0] = Math.min(bounds[0], newBounds[0]);
            bounds[1] = Math.max(bounds[1], newBounds[1]);
        }
        return bounds;
    }

    public static float convertBrightness(int min, int max, float value) {
        return (value - min) * 255 / (max - min);
    }

    private static int[] getMinMax(ImageHistogramData data, int skipPixelsCount) {
        int[] bounds = new int[]{0, 0};
        int skippedPixels = 0;
        int i = 0;
        int[] histogramData = data.getData();
        while (skippedPixels + histogramData[i] < skipPixelsCount) {
            skippedPixels += histogramData[i];
            ++i;
        }
        bounds[0] = i;
        skippedPixels = 0;
        i = histogramData.length - 1;
        while (skippedPixels + histogramData[i] < skipPixelsCount) {
            skippedPixels += histogramData[i];
            --i;
        }
        bounds[1] = i;
        return bounds;
    }
}
