package ru.itmo.grafix.core.filtering.implementation;

import java.util.stream.IntStream;

import ru.itmo.grafix.core.filtering.ConvolutionalFilter;
import ru.itmo.grafix.core.filtering.FilterType;

public class LinearAveragingFilter extends ConvolutionalFilter {

    public LinearAveragingFilter() {
        super(FilterType.LINEAR);
    }


    @Override
    protected float applyInternal(float[] data) {
        return (float) IntStream.range(0, data.length).mapToDouble(i -> data[i]).average().orElse(0.0);
    }
}
