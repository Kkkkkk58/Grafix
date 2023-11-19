package ru.itmo.grafix.core.scaling.implementation;

import ru.itmo.grafix.core.scaling.Scaling;
import ru.itmo.grafix.core.scaling.ScalingType;

public class NearestNeighbourScaling extends Scaling {
    public NearestNeighbourScaling() {
        super(ScalingType.NEAREST_NEIGHBOUR);
    }
}
