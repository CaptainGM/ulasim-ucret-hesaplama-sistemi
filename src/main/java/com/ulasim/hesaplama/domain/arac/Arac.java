package com.ulasim.hesaplama.domain.arac;

public abstract class Arac {
    public final String plaka;
    public final String tip;

    protected Arac(String plaka, String tip) {
        this.plaka = plaka;
        this.tip = tip;
    }

    public abstract double ucretHesapla(double mesafe);
    public abstract double sureHesapla(double mesafe);
}
