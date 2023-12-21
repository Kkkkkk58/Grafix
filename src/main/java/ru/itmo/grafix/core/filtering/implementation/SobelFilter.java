package ru.itmo.grafix.core.filtering.implementation;

import ru.itmo.grafix.core.filtering.Filter;
import ru.itmo.grafix.core.filtering.FilterType;
import ru.itmo.grafix.core.image.GrafixImage;

public class SobelFilter extends Filter {


    public SobelFilter() {
        super(FilterType.SOBEL);
    }

    @Override
    public boolean setParams() {
        return true;
    }

    @Override
    public GrafixImage apply(GrafixImage image) {
        return null;
    }
}
