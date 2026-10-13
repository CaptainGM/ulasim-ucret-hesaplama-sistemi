package com.ulasim.hesaplama.model;
 
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RotaSegmenti {
    private final String tip;
    private final Durak baslangicDurak;
    private final Durak bitisDurak;
    private final double mesafe;
    private final double sure;
    private final double ucret;
    private final String aracTipi;
    private final String hatBilgisi;
    private final List<Durak> duraklar;

    public RotaSegmenti(String tip, Durak baslangicDurak, Durak bitisDurak, double mesafe, double sure, double ucret, String aracTipi) {
        this(tip, baslangicDurak, bitisDurak, mesafe, sure, ucret, aracTipi, null, null);
    }

    public RotaSegmenti(String tip, Durak baslangicDurak, Durak bitisDurak, double mesafe, double sure, double ucret, String aracTipi, String hatBilgisi) {
        this(tip, baslangicDurak, bitisDurak, mesafe, sure, ucret, aracTipi, hatBilgisi, null);
    }

    public RotaSegmenti(String tip, Durak baslangicDurak, Durak bitisDurak, double mesafe, double sure, double ucret, String aracTipi, String hatBilgisi, List<Durak> duraklar) {
        this.tip = tip;
        this.baslangicDurak = baslangicDurak;
        this.bitisDurak = bitisDurak;
        this.mesafe = mesafe;
        this.sure = sure;
        this.ucret = ucret;
        this.aracTipi = aracTipi != null ? aracTipi : tip;
        this.hatBilgisi = hatBilgisi != null ? hatBilgisi :
                com.ulasim.hesaplama.service.KocaeliHatRehberi.hatBelirle(
                        this.tip, this.aracTipi,
                        baslangicDurak != null ? baslangicDurak.isim : "",
                        bitisDurak != null ? bitisDurak.isim : "");
        if (duraklar != null && !duraklar.isEmpty()) {
            this.duraklar = new ArrayList<>(duraklar);
        } else {
            List<Durak> d = new ArrayList<>();
            if (baslangicDurak != null) d.add(baslangicDurak);
            if (bitisDurak != null && !bitisDurak.equals(baslangicDurak)) d.add(bitisDurak);
            this.duraklar = d;
        }
    }

    public List<Durak> getDuraklar() {
        return Collections.unmodifiableList(duraklar);
    }

    public String getHatBilgisi() {
        return hatBilgisi;
    }

    public String getAracTipi() {
        return aracTipi;
    }

    public String getTip() {
        return tip;
    }

    public Durak getBaslangicDurak() {
        return baslangicDurak;
    }

    public Durak getBitisDurak() {
        return bitisDurak;
    }

    public double getMesafe() {
        return mesafe;
    }

    public double getSure() {
        return sure;
    }

    public double getUcret() {
        return ucret;
    }
}
