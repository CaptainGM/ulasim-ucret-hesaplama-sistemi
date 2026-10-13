package com.ulasim.hesaplama.domain.arac;

import com.ulasim.hesaplama.model.TrafikDurumu;

public class Otobus extends Arac {
    private static final double KM_UCRETI = 3.0;
    private static final double ORTALAMA_HIZ = 30.0;

    public Otobus(String plaka) {
        super(plaka, "otobus");
    }

    @Override
    public double ucretHesapla(double mesafe, TrafikDurumu trafik) {
        return mesafe * KM_UCRETI;
    }

    @Override
    public double sureHesapla(double mesafe, TrafikDurumu trafik) {
        TrafikDurumu t = (trafik != null) ? trafik : TrafikDurumu.NORMAL;
        return (mesafe / ORTALAMA_HIZ) * 60 * t.getSureKatsayisi();
    }
}

