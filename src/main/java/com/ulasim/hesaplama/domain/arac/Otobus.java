package com.ulasim.hesaplama.domain.arac;

public class Otobus extends Arac {
    private static final double KM_UCRETI = 3.0;
    private static final double ORTALAMA_HIZ = 30.0;

    public Otobus(String plaka) {
        super(plaka, "otobus");
    }

    @Override
    public double ucretHesapla(double mesafe) {
        return mesafe * KM_UCRETI;
    }

    @Override
    public double sureHesapla(double mesafe) {
        return (mesafe / ORTALAMA_HIZ) * 60;
    }
}
