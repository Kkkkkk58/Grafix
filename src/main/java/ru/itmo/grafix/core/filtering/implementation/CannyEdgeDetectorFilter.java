package ru.itmo.grafix.core.filtering.implementation;

import ru.itmo.grafix.core.filtering.Filter;
import ru.itmo.grafix.core.filtering.FilterType;

public class CannyEdgeDetectorFilter extends Filter {
    public CannyEdgeDetectorFilter() {
        super(FilterType.CANNY_EDGE_DETECTOR);
    }

    @Override
    public boolean setParams() {
        return true;
    }
}
