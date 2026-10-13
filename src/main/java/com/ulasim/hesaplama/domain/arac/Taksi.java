package com.ulasim.hesaplama.domain.arac;

import com.ulasim.hesaplama.model.TrafikDurumu;

public class Taksi extends Arac {
    private static final double ACILIS_UCRETI = 55.0;
    private static final double KM_UCRETI = 55.0;
    private static final double MINIMUM_INDI_BINDI_UCRETI = 200.0;
    private static final double DAKIKA_BEKLEME_UCRETI = 5.50; // TL/dk
    private static final double ORTALAMA_HIZ = 35.0; // km/s

    public Taksi(String plaka) {
        super(plaka, "taksi");
    }

    @Override
    public double ucretHesapla(double mesafe, TrafikDurumu trafik) {
        TrafikDurumu t = (trafik != null) ? trafik : TrafikDurumu.NORMAL;
        // Normal seyahat süresi ve trafikten kaynaklı ek bekleme süresi
        double normalSure = (mesafe / ORTALAMA_HIZ) * 60;
        double trafikliSure = normalSure * t.getSureKatsayisi();
        double beklemeSuresiDakika = Math.max(0, trafikliSure - normalSure);

        // Taksimetre = Açılış + (Mesafe * KM) + (Trafikte Dur-Kalk Bekleme Ücreti)
        double hesaplanan = ACILIS_UCRETI + (mesafe * KM_UCRETI) + (beklemeSuresiDakika * DAKIKA_BEKLEME_UCRETI);

        // Kocaeli UKOME kuralı: Kısa mesafe indi-bindi taban ücretinden (200 TL) az olamaz
        return Math.max(MINIMUM_INDI_BINDI_UCRETI, Math.round(hesaplanan * 10.0) / 10.0);
    }

    @Override
    public double sureHesapla(double mesafe, TrafikDurumu trafik) {
        TrafikDurumu t = (trafik != null) ? trafik : TrafikDurumu.NORMAL;
        return (mesafe / ORTALAMA_HIZ) * 60 * t.getSureKatsayisi();
    }
}

