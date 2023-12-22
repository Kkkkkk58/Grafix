package ru.itmo.grafix.core.filtering.implementation;

import java.util.Arrays;

import ru.itmo.grafix.core.filtering.FilterType;
import ru.itmo.grafix.core.filtering.ThresholdFilter;
import ru.itmo.grafix.core.image.GrafixImage;
import ru.itmo.grafix.ui.components.dialogs.filters.ThresholdChoiceDialog;

public class SimpleThresholdFilter extends ThresholdFilter {
    private int[] thresholdValues;
    public SimpleThresholdFilter() {
        super(FilterType.THRESHOLD);
    }

    @Override
    public boolean setParams() {
        ThresholdChoiceDialog choiceDialog = new ThresholdChoiceDialog();
        thresholdValues = choiceDialog.showAndWait().orElse(null);
        return thresholdValues != null;
    }

    @Override
    protected double[] getThresholds() {
        return Arrays.stream(thresholdValues).mapToDouble(t -> t / 255.0).toArray();
    }
}
