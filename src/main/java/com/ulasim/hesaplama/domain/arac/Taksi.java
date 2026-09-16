package com.ulasim.hesaplama.domain.arac;

public class Taksi extends Arac {
    private static final double ACILIS_UCRETI = 10.0;
    private static final double KM_UCRETI = 4.0;
    private static final double ORTALAMA_HIZ = 40.0;

    public Taksi(String plaka) {
        super(plaka, "taksi");
    }

    @Override
    public double ucretHesapla(double mesafe) {
        return ACILIS_UCRETI + (mesafe * KM_UCRETI);
    }

    @Override
    public double sureHesapla(double mesafe) {
        return (mesafe / ORTALAMA_HIZ) * 60;
    }
}
