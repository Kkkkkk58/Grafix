package ru.itmo.grafix.core.autocorrection;

import ru.itmo.grafix.core.image.GrafixImage;

import java.util.ArrayList;
import java.util.List;

public class ImageHistogramExtractor {
    public static List<ImageHistogramData> getImageHistogramData(GrafixImage image) {
        int k = image.getTotalChannels();
        int bytesPerPixel = k;
        int offset = 0;
        if (image.getChannel() != 0) {
            k = 1;
            offset = image.getChannel() - 1;
        }
        List<int[]> histData = new ArrayList<>(k);
        for (int i = 0; i < k; ++i) {
            histData.add(new int[256]);
        }

        float[] data = image.getData();
        for (int i = 0; i < image.getHeight(); ++i) {
            for (int j = 0; j < image.getWidth(); ++j) {
                for (int c = 0; c < k; ++c) {
                    int index = bytesPerPixel * (i * image.getWidth() + j) + c + offset;
                    int pixelValue = (int) (image.getColorSpace().normalize(data[index]) * 255);
                    ++histData.get(c)[pixelValue];
                }
            }
        }

        if (k != 1) {
            int[] generalHistogramData = getGeneralHistogramData(histData);
            histData.add(generalHistogramData);
        }

        return histData.stream().map(ImageHistogramData::new).toList();
    }

    private static int[] getGeneralHistogramData(List<int[]> histData) {
        int[] generalHistogramData = new int[256];
        for (int[] hist : histData) {
            for (int i = 0; i < hist.length; ++i) {
                generalHistogramData[i] += hist[i];
            }
        }
        return generalHistogramData;
    }
}
