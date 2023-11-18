package ru.itmo.grafix.core.scaling.implementation;

import ru.itmo.grafix.core.image.GrafixImage;
import ru.itmo.grafix.core.scaling.Scaling;
import ru.itmo.grafix.core.scaling.ScalingType;

public class BilinearScaling extends Scaling {
    public BilinearScaling() {
        super(ScalingType.BILINEAR);
    }

    @Override
    public GrafixImage applyScaling(GrafixImage oldImage, int width, int height) {
        return null;
    }
}
