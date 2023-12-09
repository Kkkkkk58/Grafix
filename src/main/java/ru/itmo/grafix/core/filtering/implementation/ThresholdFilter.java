package ru.itmo.grafix.core.filtering.implementation;

import ru.itmo.grafix.core.filtering.Filter;
import ru.itmo.grafix.core.filtering.FilterType;
import ru.itmo.grafix.ui.components.dialogs.filters.ThresholdChoiceDialog;

public class ThresholdFilter extends Filter {
    private int[] thresholdValues;
    public ThresholdFilter() {
        super(FilterType.THRESHOLD);
    }

    @Override
    public boolean setParams() {
        ThresholdChoiceDialog choiceDialog = new ThresholdChoiceDialog();
        thresholdValues = choiceDialog.showAndWait().orElse(null);
        return thresholdValues != null;
    }

    public int[] getThresholdValues() {
        return thresholdValues;
    }

    public void setThresholdValues(int[] thresholdValues) {
        this.thresholdValues = thresholdValues;
    }
}
