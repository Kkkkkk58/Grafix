package ru.itmo.grafix.core.filtering.implementation;

import ru.itmo.grafix.core.filtering.Filter;
import ru.itmo.grafix.core.filtering.FilterType;
import ru.itmo.grafix.ui.components.dialogs.filters.SigmaChoiceDialog;

public class GaussianFilter extends Filter {
    private double sigma;

    public GaussianFilter() {
        super(FilterType.GAUSSIAN);
    }

    public void setSigma(double sigma) {
        this.sigma = sigma;
    }

    public double getSigma() {
        return sigma;
    }
    @Override
    public boolean setParams() {
        Double s = SigmaChoiceDialog.getSigmaInput();
        if(s == null){
            return false;
        }
        sigma = s;
        return true;
    }
}