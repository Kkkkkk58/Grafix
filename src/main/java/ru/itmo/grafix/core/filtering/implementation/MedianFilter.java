package ru.itmo.grafix.core.filtering.implementation;

import java.util.Arrays;

import ru.itmo.grafix.core.filtering.ConvolutionalFilter;
import ru.itmo.grafix.core.filtering.FilterType;

public class MedianFilter extends ConvolutionalFilter {

    public MedianFilter() {
        super(FilterType.MEDIAN);
    }

    @Override
    protected float applyInternal(float[] data) {
        Arrays.sort(data);
        return (float) getMedian(data);
    }

    private double getMedian(float[] data) {
        return (data.length % 2 == 0)
                ? ((double) data[data.length / 2] + (double) data[data.length / 2 - 1]) / 2.0
                : data[data.length / 2];
    }
}