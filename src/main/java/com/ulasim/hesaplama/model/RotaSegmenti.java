package com.ulasim.hesaplama.model;

public class RotaSegmenti {
    private final String tip;
    private final Durak baslangicDurak;
    private final Durak bitisDurak;
    private final double mesafe;
    private final double sure;
    private final double ucret;
    private final String aracTipi;

    public RotaSegmenti(String tip, Durak baslangicDurak, Durak bitisDurak, double mesafe, double sure, double ucret, String aracTipi) {
        this.tip = tip;
        this.baslangicDurak = baslangicDurak;
        this.bitisDurak = bitisDurak;
        this.mesafe = mesafe;
        this.sure = sure;
        this.ucret = ucret;
        this.aracTipi = aracTipi != null ? aracTipi : tip;
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
