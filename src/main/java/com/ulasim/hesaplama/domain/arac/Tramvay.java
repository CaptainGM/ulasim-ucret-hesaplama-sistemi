package com.ulasim.hesaplama.domain.arac;

public class Tramvay extends Arac {
    private static final double KM_UCRETI = 2.5;
    private static final double ORTALAMA_HIZ = 25.0;

    public Tramvay(String plaka) {
        super(plaka, "tramvay");
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
