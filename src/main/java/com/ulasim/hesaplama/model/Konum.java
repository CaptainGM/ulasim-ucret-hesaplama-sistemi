package com.ulasim.hesaplama.model;

public class Konum {
    private final double enlem;
    private final double boylam;

    public Konum(double enlem, double boylam) {
        this.enlem = enlem;
        this.boylam = boylam;
    }

    public double getEnlem() {
        return enlem;
    }

    public double getBoylam() {
        return boylam;
    }

    @Override
    public String toString() {
        return "(" + enlem + ", " + boylam + ")";
    }
}
