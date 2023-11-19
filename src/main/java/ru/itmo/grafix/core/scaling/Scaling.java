package ru.itmo.grafix.core.scaling;

public abstract class Scaling {

    private final ScalingType type;

    protected Scaling(ScalingType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type.getName();
    }

    public ScalingType getType(){
        return this.type;
    }
}
