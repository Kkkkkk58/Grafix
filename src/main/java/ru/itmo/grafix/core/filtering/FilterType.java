package ru.itmo.grafix.core.filtering;

public enum FilterType {
    LINEAR("Linear averaging"),
    THRESHOLD("Threshold"),
    THRESHOLD_OTSU("Threshold by Otsu"),
    MEDIAN("Median"),

    GAUSSIAN("Gaussian"),
    UNSHARP_MASKING("Unsharp masking"),
    CONTRAST_ADAPTIVE_SHARPENING("Contrast adaptive sharpening"),
    SOBEL("Sobel"),
    CANNY_EDGE_DETECTOR("Canny edge detector");

    private final String name;

    FilterType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
