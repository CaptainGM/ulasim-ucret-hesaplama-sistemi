package com.ulasim.hesaplama.domain.arac;

import com.ulasim.hesaplama.model.TrafikDurumu;

public class Tramvay extends Arac {
    private static final double KM_UCRETI = 2.5;
    private static final double ORTALAMA_HIZ = 25.0;

    public Tramvay(String plaka) {
        super(plaka, "tramvay");
    }

    @Override
    public double ucretHesapla(double mesafe, TrafikDurumu trafik) {
        return mesafe * KM_UCRETI;
    }

    @Override
    public double sureHesapla(double mesafe, TrafikDurumu trafik) {
        // Tramvay raylı sistem olduğu için karayolu trafiğinden etkilenmez!
        return (mesafe / ORTALAMA_HIZ) * 60;
    }
}

