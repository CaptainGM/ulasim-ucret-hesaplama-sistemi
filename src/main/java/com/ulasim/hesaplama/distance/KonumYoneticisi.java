package com.ulasim.hesaplama.distance;

import com.ulasim.hesaplama.model.Konum;

public class KonumYoneticisi {
    private static KonumYoneticisi instance;
    private MesafeHesaplamaStratejisi mesafeHesaplayici;

    private KonumYoneticisi() {
        mesafeHesaplayici = new HaversineMesafeHesaplayici();
    }

    public static KonumYoneticisi getInstance() {
        if (instance == null) {
            instance = new KonumYoneticisi();
        }
        return instance;
    }

    public void setMesafeHesaplayici(MesafeHesaplamaStratejisi hesaplayici) {
        this.mesafeHesaplayici = hesaplayici;
    }

    public double mesafeHesapla(Konum k1, Konum k2) {
        return mesafeHesaplayici.mesafeHesapla(k1, k2);
    }

    public String getAktifStrateji() {
        return mesafeHesaplayici.getStratejiAdi();
    }
}
