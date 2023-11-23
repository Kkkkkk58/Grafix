package ru.itmo.grafix.core.scaling;

import ru.itmo.grafix.core.image.GrafixImage;
import ru.itmo.grafix.core.imageprocessing.PixelValueNormalizer;

public abstract class ScalingWithKernel extends Scaling {

    private final int support;

    protected ScalingWithKernel(ScalingType type, int support) {
        super(type);
        this.support = support;
    }

    @Override
    public GrafixImage applyScaling(GrafixImage oldImage, int width, int height, float biasX, float biasY) {
        double scaleX = width * 1.0 / oldImage.getWidth();
        double scaleY = height * 1.0 / oldImage.getHeight();

        float[] data;
        if (Math.abs(scaleX - 1) <= Math.abs(scaleY - 1)) {
            data = applyScalingOnX(oldImage.getHeight(), oldImage.getWidth(), oldImage.getTotalChannels(), width, oldImage.getHeight(),
                    oldImage.getData(), biasX);
            data = applyScalingOnY(oldImage.getHeight(), width, oldImage.getTotalChannels(), width, height, data, biasY);
        } else {
            data = applyScalingOnY(oldImage.getHeight(), oldImage.getWidth(), oldImage.getTotalChannels(),
                    oldImage.getWidth(), height, oldImage.getData(), biasY);
            data = applyScalingOnX(height, oldImage.getWidth(), oldImage.getTotalChannels(), width, height, data, biasX);
        }

        return new GrafixImage(oldImage.getFormat(), width, height, oldImage.getMaxVal(),
                data, oldImage.getPath(), oldImage.getHeaderSize(), oldImage.getColorSpace());
    }

    private float[] applyScalingOnY(int oldHeight, int oldWidth, int totalChannels, int width, int height,
                                    float[] oldData, float biasY) {
        if (oldHeight == height && biasY == 0) {
            return oldData;
        }
        int offset = (int) biasY;
        biasY -= offset;

        float[] data = new float[width * height * totalChannels];
        double scale = Math.min(1, (double) height / oldHeight);
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                double centerX = (j + 0.5) / height * oldHeight - biasY;
                double filterStart = centerX - support;
                double start = filterStart - 0.5;
                for (int ch = 0; ch < totalChannels; ch++) {
                    double val = 0;
                    double coeffSum = 0;
                    for (int k = 0; k < support * 2 + 1; ++k) {
                        double inputX = start + k + biasY;
                        double x = (inputX + 0.5 - centerX - 2 * biasY) * scale;
                        double y = (inputX + 0.5 - filterStart - 2 * biasY) / (support * 2);
                        double coeff = getKernel(x) * (0 <= y && y <= 1 ? 1 - Math.abs(y - 0.5) * 2 : 0);
                        x = Math.min(Math.max(inputX, 0), oldHeight - 1);
                        val += (PixelValueNormalizer.normalize((float) (coeff * oldData[getLinearCoordinate(i,
                                (int) x, ch, oldWidth, oldHeight, totalChannels)])));
                        coeffSum += coeff;
                    }
                    data[getLinearCoordinate(i, j - offset, ch, width, height, totalChannels)] =
                            (PixelValueNormalizer.normalize((float) (val / coeffSum)));
                }
            }
        }
        return data;
    }

    protected abstract float getKernel(double d);

    private float[] applyScalingOnX(int oldHeight, int oldWidth, int totalChannels, int width, int height, float[] oldData, float biasX) {
        if (oldWidth == width && biasX == 0) {
            return oldData;
        }
        int offset = (int) biasX;
        biasX -= offset;

        float[] data = new float[width * height * totalChannels];
        double scale = Math.min(1, (double) width / oldWidth);
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                double centerX = (j + 0.5) / width * oldWidth - biasX;
                double filterStart = centerX - support;
                double start = filterStart - 0.5;
                for (int ch = 0; ch < totalChannels; ch++) {
                    double val = 0;
                    double coeffSum = 0;
                    for (int k = 0; k < support * 2 + 1; ++k) {
                        double inputX = start + k + biasX;
                        double x = (inputX + 0.5 - centerX - 2 * biasX) * scale;
                        double y = (inputX + 0.5 - filterStart - 2 * biasX) / (support * 2);
                        double coeff = getKernel(x) * (0 <= y && y <= 1 ? 1 - Math.abs(y - 0.5) * 2 : 0);
                        x = Math.min(Math.max(inputX, 0), oldWidth - 1);
                        val += (PixelValueNormalizer.normalize((float) (coeff * oldData[getLinearCoordinate((int) x,
                                i, ch, oldWidth, oldHeight, totalChannels)])));
                        coeffSum += coeff;
                    }
                    data[getLinearCoordinate(j - offset, i, ch, width, height, totalChannels)] =
                            (PixelValueNormalizer.normalize((float) (val / coeffSum)));
                }
            }
        }
        return data;
    }

}
