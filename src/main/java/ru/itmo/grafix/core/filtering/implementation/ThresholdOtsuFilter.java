package ru.itmo.grafix.core.filtering.implementation;

import ru.itmo.grafix.core.filtering.Filter;
import ru.itmo.grafix.core.filtering.FilterType;
import ru.itmo.grafix.core.image.GrafixImage;
import ru.itmo.grafix.ui.components.dialogs.filters.ThresholdOtsuChoiceDialog;

public class ThresholdOtsuFilter extends Filter {
    private Integer thresholdCount;
    public ThresholdOtsuFilter() {
        super(FilterType.THRESHOLD_OTSU);
    }

    @Override
    public boolean setParams() {
        ThresholdOtsuChoiceDialog choiceDialog = new ThresholdOtsuChoiceDialog();
        thresholdCount = choiceDialog.showAndWait().orElse(null);
        return thresholdCount != null;
    }

    @Override
    public GrafixImage apply(GrafixImage image) {
        return null;
    }

    public int getThresholdCount() {
        return thresholdCount;
    }

    public void setThresholdCount(int thresholdValues) {
        this.thresholdCount = thresholdCount;
    }

}
