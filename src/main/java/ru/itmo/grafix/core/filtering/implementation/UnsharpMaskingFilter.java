package ru.itmo.grafix.core.filtering.implementation;

import ru.itmo.grafix.core.filtering.Filter;
import ru.itmo.grafix.core.filtering.FilterType;
import ru.itmo.grafix.ui.components.dialogs.filters.UnsharpMaskingParamsDialog;
import ru.itmo.grafix.ui.models.UnsharpMaskingParams;

public class UnsharpMaskingFilter extends Filter {
    private UnsharpMaskingParams params;

    public UnsharpMaskingParams getParams() {
        return params;
    }

    public void setParams(UnsharpMaskingParams params) {
        this.params = params;
    }

    public UnsharpMaskingFilter() {
        super(FilterType.UNSHARP_MASKING);
    }
    @Override
    public boolean setParams() {
        UnsharpMaskingParamsDialog paramsChoiceDialog = new UnsharpMaskingParamsDialog();
        params = paramsChoiceDialog.showAndWait().orElse(null);
        return params != null;
    }
}
