package ru.itmo.grafix.core.scaling.implementation;

import ru.itmo.grafix.core.image.GrafixImage;
import ru.itmo.grafix.core.scaling.Scaling;
import ru.itmo.grafix.core.scaling.ScalingType;

public class Lanczos3Scaling extends Scaling {
    public Lanczos3Scaling() {
        super(ScalingType.LANCZOS3);
    }

    @Override
    public GrafixImage applyScaling(GrafixImage oldImage, int width, int height) {
        return null;
    }
}
