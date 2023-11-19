package ru.itmo.grafix.core.scaling;

public enum ScalingType {
    BC_SPLINE("BC-spline"),
    BILINEAR("Bilinear"),
    LANCZOS3("Lanczos 3"),
    NEAREST_NEIGHBOUR("Nearest neighbour");

    private final String name;

    ScalingType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
