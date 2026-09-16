package com.ulasim.hesaplama.domain.yolcu;

import com.ulasim.hesaplama.payment.OdemeStratejisi;

public class Ogrenci extends Yolcu {
    public Ogrenci(String isim, OdemeStratejisi odeme) {
        super(isim, "Öğrenci", odeme);
    }

    @Override
    public double indirimHesapla(double ucret, String aracTipi) {
        if ("otobus".equals(aracTipi) || "tramvay".equals(aracTipi)) {
            return ucret * 0.5;
        }
        return ucret;
    }
}
