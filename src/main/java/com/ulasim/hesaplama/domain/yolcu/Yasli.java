package com.ulasim.hesaplama.domain.yolcu;

import com.ulasim.hesaplama.payment.OdemeStratejisi;

public class Yasli extends Yolcu {
    private static final int MAKSIMUM_UCRETSIZ_KULLANIM = 20;
    private int kullanimSayisi = 0;

    public Yasli(String isim, OdemeStratejisi odeme) {
        super(isim, "Yaşlı", odeme);
    }

    @Override
    public double indirimHesapla(double ucret, String aracTipi) {
        if ("otobus".equals(aracTipi) || "tramvay".equals(aracTipi)) {
            if (kullanimSayisi < MAKSIMUM_UCRETSIZ_KULLANIM) {
                kullanimSayisi++;
                return 0;
            }
            return ucret * 0.5;
        }
        return ucret;
    }
}
