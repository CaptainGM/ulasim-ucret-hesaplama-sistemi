package com.ulasim.hesaplama.domain.arac;

import com.ulasim.hesaplama.model.TrafikDurumu;

public abstract class Arac {
    public final String plaka;
    public final String tip;

    protected Arac(String plaka, String tip) {
        this.plaka = plaka;
        this.tip = tip;
    }

    public double ucretHesapla(double mesafe) {
        return ucretHesapla(mesafe, TrafikDurumu.NORMAL);
    }

    public double sureHesapla(double mesafe) {
        return sureHesapla(mesafe, TrafikDurumu.NORMAL);
    }

    public abstract double ucretHesapla(double mesafe, TrafikDurumu trafik);
    public abstract double sureHesapla(double mesafe, TrafikDurumu trafik);
}

