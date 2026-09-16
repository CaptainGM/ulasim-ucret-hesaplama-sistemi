package com.ulasim.hesaplama.domain.yolcu;

import com.ulasim.hesaplama.payment.OdemeStratejisi;

public abstract class Yolcu {
    public final String isim;
    public final String tip;
    public final OdemeStratejisi odeme;

    protected Yolcu(String isim, String tip, OdemeStratejisi odeme) {
        this.isim = isim;
        this.tip = tip;
        this.odeme = odeme;
    }

    public abstract double indirimHesapla(double ucret, String aracTipi);
}
