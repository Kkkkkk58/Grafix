package ru.itmo.grafix.ui.models;

public class UnsharpMaskingParams {
    public UnsharpMaskingParams(double amount, double sigma, int threshold) {
        this.amount = amount;
        this.sigma = sigma;
        this.threshold = threshold;
    }

    private double amount = 0d;
    private double sigma = 0.1;
    private int threshold = 0;


    public void setSigma(double sigma) {
        this.sigma = sigma;
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

    public double getSigma() {
        return sigma;
    }

    public int getThreshold() {
        return threshold;
    }

}
