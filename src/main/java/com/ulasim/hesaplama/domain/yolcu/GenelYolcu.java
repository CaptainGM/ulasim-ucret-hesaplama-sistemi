package com.ulasim.hesaplama.domain.yolcu;

import com.ulasim.hesaplama.payment.OdemeStratejisi;

public class GenelYolcu extends Yolcu {
    public GenelYolcu(String isim, OdemeStratejisi odeme) {
        super(isim, "Genel", odeme);
    }

    @Override
    public double indirimHesapla(double ucret, String aracTipi) {
        return ucret;
    }
}
