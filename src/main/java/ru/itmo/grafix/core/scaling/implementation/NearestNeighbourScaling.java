package ru.itmo.grafix.core.scaling.implementation;

import ru.itmo.grafix.core.image.GrafixImage;
import ru.itmo.grafix.core.scaling.Scaling;
import ru.itmo.grafix.core.scaling.ScalingType;

public class NearestNeighbourScaling extends Scaling {
    public NearestNeighbourScaling() {
        super(ScalingType.NEAREST_NEIGHBOUR);
    }

    @Override
    public GrafixImage applyScaling(GrafixImage oldImage, int width, int height, float biasX, float biasY){
        float[] data = new float[width * height * oldImage.getTotalChannels()];
        double sx = oldImage.getWidth() * 1.0 / width;
        double sy = oldImage.getHeight() * 1.0 / height;
        int offsetY = (int) biasY;
        biasY -= offsetY;
        int offsetX = (int) biasX;
        biasX -= offsetX;

        for(int i = 0; i < height; ++i){
            for(int j = 0; j < width; ++j){
                for(int k = 0; k < oldImage.getTotalChannels(); ++k){
                    int coordinate = getLinearCoordinate(j - offsetX, i - offsetY, k, width, height, oldImage.getTotalChannels());
                    int oldCoordinate = getOldCoordinate(oldImage, sx, sy, i, j, k, biasX, biasY);
                    data[coordinate] = oldImage.getData()[oldCoordinate];
                }
            }
        }
        return new GrafixImage(oldImage.getFormat(), width, height, oldImage.getMaxVal(),
                data, oldImage.getPath(), oldImage.getHeaderSize(), oldImage.getColorSpace());
    }

    private int getOldCoordinate(GrafixImage oldImage, double sx, double sy, int i, int j, int k, float biasX, float biasY) {
        int oldX = (int) Math.floor(sx * j + 2 * biasX);
        int oldY = (int) Math.floor(sy * i + 2 * biasY);
        return getLinearCoordinate(oldX, oldY, k, oldImage.getWidth(), oldImage.getHeight(), oldImage.getTotalChannels());
    }
}
