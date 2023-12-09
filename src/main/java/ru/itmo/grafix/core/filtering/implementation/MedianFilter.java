package ru.itmo.grafix.core.filtering.implementation;

import ru.itmo.grafix.core.filtering.Filter;
import ru.itmo.grafix.core.filtering.FilterType;
import ru.itmo.grafix.ui.components.dialogs.filters.RadiusChoiceDialog;

public class MedianFilter extends Filter {
    private int radius = 1;

    public MedianFilter() {
        super(FilterType.MEDIAN);
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }
    @Override
    public boolean setParams() {
        Integer r = RadiusChoiceDialog.getRadiusInput();
        if(r == null){
            return false;
        }
        radius = r;
        return true;
    }
}