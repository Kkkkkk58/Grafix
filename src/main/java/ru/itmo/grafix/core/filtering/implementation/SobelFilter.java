package ru.itmo.grafix.core.filtering.implementation;

import ru.itmo.grafix.core.filtering.Filter;
import ru.itmo.grafix.core.filtering.FilterType;

public class SobelFilter extends Filter {


    public SobelFilter() {
        super(FilterType.SOBEL);
    }

    @Override
    public boolean setParams() {
        return true;
    }
}
