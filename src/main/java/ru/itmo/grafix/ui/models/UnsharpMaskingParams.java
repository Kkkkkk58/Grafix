package ru.itmo.grafix.ui.models;

public class UnsharpMaskingParams {
    public UnsharpMaskingParams(double amount, double radius, int threshold) {
        this.amount = amount;
        this.radius = radius;
        this.threshold = threshold;
    }

    private double amount = 0d;
    private double radius = 0.1;
    private int threshold = 0;


    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
    public double getAmount() {
        return amount;
    }

    public double getRadius() {
        return radius;
    }

    public int getThreshold() {
        return threshold;
    }

}
