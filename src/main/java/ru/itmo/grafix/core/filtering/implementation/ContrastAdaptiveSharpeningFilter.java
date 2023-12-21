package ru.itmo.grafix.core.filtering.implementation;

import ru.itmo.grafix.core.filtering.Filter;
import ru.itmo.grafix.core.filtering.FilterType;
import ru.itmo.grafix.core.image.GrafixImage;
import ru.itmo.grafix.ui.components.dialogs.filters.SharpnessChoiceDialog;

public class ContrastAdaptiveSharpeningFilter extends Filter {
    private double sharpness;

    public ContrastAdaptiveSharpeningFilter() {
        super(FilterType.CONTRAST_ADAPTIVE_SHARPENING);
    }

    public void setSharpness(double sharpness) {
        this.sharpness = sharpness;
    }

    public double getSharpness() {
        return sharpness;
    }

    @Override
    public boolean setParams() {
        Double s = SharpnessChoiceDialog.getSharpnessInput();
        if(s == null){
            return false;
        }
        sharpness = s;
        return true;
    }

    @Override
    public GrafixImage apply(GrafixImage image) {
        return null;
    }
}
