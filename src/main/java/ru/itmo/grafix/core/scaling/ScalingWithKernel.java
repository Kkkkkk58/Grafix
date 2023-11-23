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
    public GrafixImage applyScaling(GrafixImage oldImage, int width, int height) {
        double scaleX = width * 1.0 / oldImage.getWidth();
        double scaleY = height * 1.0 / oldImage.getHeight();

        float[] data;
        if (Math.abs(scaleX - 1) <= Math.abs(scaleY - 1)) {
            data = applyScalingOnX(oldImage.getWidth(), oldImage.getTotalChannels(), width, oldImage.getHeight(), oldImage.getData());
            data = applyScalingOnY(oldImage.getHeight(), width, oldImage.getTotalChannels(), width, height, data);
        } else {
            data = applyScalingOnY(oldImage.getHeight(), oldImage.getWidth(), oldImage.getTotalChannels(), oldImage.getWidth(), height, oldImage.getData());
            data = applyScalingOnX(oldImage.getWidth(), oldImage.getTotalChannels(), width, height, data);
        }

        return new GrafixImage(oldImage.getFormat(), width, height, oldImage.getMaxVal(),
                data, oldImage.getPath(), oldImage.getHeaderSize(), oldImage.getColorSpace());
    }

    private float[] applyScalingOnY(int oldHeight, int oldWidth, int totalChannels, int width, int height, float[] oldData) {
        if (oldHeight == height) {
            return oldData;
        }
        float[] data = new float[width * height * totalChannels];
        double scale = Math.min(1, (double) height / oldHeight);
        for(int i = 0; i < width; ++i){
            for(int j = 0; j < height; ++j){
                double centerX = (j + 0.5) / height * oldHeight;
                double filterStart = centerX - support;
                int start = (int) Math.ceil(filterStart - 0.5);
                for(int ch = 0; ch < totalChannels; ch++) {
                    double val = 0;
                    double coeffSum = 0;
                    for(int k = 0; k < support * 2 + 1; ++k){
                        int inputX = start + k;
                        double x = (inputX + 0.5 - centerX) * scale;
                        double y = (inputX + 0.5 - filterStart) / (support * 2);
                        double coeff = getKernel(x) * (0 <= y && y <=  1 ? 1 - Math.abs(y - 0.5) * 2 : 0);
                        x = Math.min(Math.max(inputX, 0), oldHeight - 1);
                        val += (PixelValueNormalizer.normalize((float) (coeff * oldData[getLinearCoordinate(i, (int) x, ch, oldWidth, totalChannels)])));
                        coeffSum += coeff;
                    }
                    data[getLinearCoordinate(i, j, ch, width, totalChannels)] = (PixelValueNormalizer.normalize((float) (val / coeffSum)));
                }
            }
        }
        return data;
    }

    protected abstract float getKernel(double d);

    private float[] applyScalingOnX(int oldWidth, int totalChannels, int width, int height, float[] oldData){
        if (oldWidth == width) {
            return oldData;
        }
        float[] data = new float[width * height * totalChannels];
        double scale = Math.min(1, (double) width / oldWidth );
        for(int i = 0; i < height; ++i){
            for(int j = 0; j < width; ++j){
                double centerX = (j + 0.5) / width * oldWidth;
                double filterStart = centerX - support;
                int start = (int) Math.ceil(filterStart - 0.5);
                for(int ch = 0; ch < totalChannels; ch++) {
                    double val = 0;
                    double coeffSum = 0;
                    for(int k = 0; k < support * 2 + 1; ++k){
                        int inputX = start + k;
                        double x = (inputX + 0.5 - centerX) * scale;
                        double y = (inputX + 0.5 - filterStart) / (support * 2);
                        double coeff = getKernel(x) * (0 <= y && y <=  1 ? 1 - Math.abs(y - 0.5) * 2 : 0);
                        x = Math.min(Math.max(inputX, 0), oldWidth - 1);
                        val += (PixelValueNormalizer.normalize((float) (coeff * oldData[getLinearCoordinate((int) x, i, ch, oldWidth, totalChannels)])));
                        coeffSum += coeff;
                    }
                    data[getLinearCoordinate(j, i, ch, width, totalChannels)] = (PixelValueNormalizer.normalize((float) (val / coeffSum)));
                }
            }
        }
        return data;
    }

}
