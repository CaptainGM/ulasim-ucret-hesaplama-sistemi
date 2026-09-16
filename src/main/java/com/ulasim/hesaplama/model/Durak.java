package com.ulasim.hesaplama.model;

import com.ulasim.hesaplama.distance.KonumYoneticisi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Durak {
    public final String id;
    public final String isim;
    public final String tip;
    private final Konum konum;
    public final boolean sonDurak;

    private final List<Baglanti> baglantilar = new ArrayList<>();

    public Durak(String id, String isim, String tip, double enlem, double boylam, boolean sonDurak) {
        this.id = id;
        this.isim = isim;
        this.tip = tip;
        this.konum = new Konum(enlem, boylam);
        this.sonDurak = sonDurak;
    }

    public void baglantiEkle(Baglanti baglanti) {
        baglantilar.add(baglanti);
    }

    public List<Baglanti> getBaglantilar() {
        return baglantilar;
    }

    public Konum getKonum() {
        return konum;
    }

    public double mesafeHesapla(Konum digerKonum) {
        return KonumYoneticisi.getInstance().mesafeHesapla(this.konum, digerKonum);
    }

    @Override
    public String toString() {
        return isim;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Durak durak = (Durak) obj;
        return id.equals(durak.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
