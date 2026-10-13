package com.ulasim.hesaplama.domain.arac;

import com.ulasim.hesaplama.model.TrafikDurumu;

public class Taksi extends Arac {
    private static final double ACILIS_UCRETI = 35.0;
    private static final double KM_UCRETI = 25.0;
    private static final double MINIMUM_INDI_BINDI_UCRETI = 100.0;
    private static final double ORTALAMA_HIZ = 35.0; // km/s

    public Taksi(String plaka) {
        super(plaka, "taksi");
    }

    @Override
    public double ucretHesapla(double mesafe, TrafikDurumu trafik) {
        TrafikDurumu t = (trafik != null) ? trafik : TrafikDurumu.NORMAL;
        // Taksimetre açılış + km ücreti * trafik dur-kalk maliyet katsayısı
        double hesaplanan = ACILIS_UCRETI + (mesafe * KM_UCRETI * t.getTaksiMaliyetKatsayisi());
        // Türkiye taksi yönetmeliği: indi-bindi taban ücretinden az olamaz
        return Math.max(MINIMUM_INDI_BINDI_UCRETI, Math.round(hesaplanan * 10.0) / 10.0);
    }

    @Override
    public double sureHesapla(double mesafe, TrafikDurumu trafik) {
        TrafikDurumu t = (trafik != null) ? trafik : TrafikDurumu.NORMAL;
        return (mesafe / ORTALAMA_HIZ) * 60 * t.getSureKatsayisi();
    }
}

