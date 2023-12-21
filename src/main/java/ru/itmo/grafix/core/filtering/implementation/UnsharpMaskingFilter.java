package ru.itmo.grafix.core.filtering.implementation;

import ru.itmo.grafix.core.filtering.ConvolutionalFilter;
import ru.itmo.grafix.core.filtering.FilterType;
import ru.itmo.grafix.ui.components.dialogs.filters.UnsharpMaskingParamsDialog;
import ru.itmo.grafix.ui.models.UnsharpMaskingParams;

public class UnsharpMaskingFilter extends ConvolutionalFilter {
    private final GaussianFilter gaussianFilter = new GaussianFilter();
    private UnsharpMaskingParams params;

    public UnsharpMaskingParams getParams() {
        return params;
    }

    public UnsharpMaskingFilter() {
        super(FilterType.UNSHARP_MASKING);
    }
    @Override
    public boolean setParams() {
        UnsharpMaskingParamsDialog paramsChoiceDialog = new UnsharpMaskingParamsDialog();
        params = paramsChoiceDialog.showAndWait().orElse(null);
        if (params == null) {
            return false;
        }
        gaussianFilter.setSigma(params.getSigma());
        setRadius(gaussianFilter.getRadius());
        return true;
    }

    @Override
    protected float applyInternal(float[] data) {
        float smoothed = gaussianFilter.applyInternal(data);
        float originalValue = data[data.length / 2];
        float difference = originalValue - smoothed;

        // FIXME normalize amount
        return (float) (originalValue +
                ((Math.abs(difference) >= params.getThreshold()) ? params.getAmount() * difference : 0));
    }
}
