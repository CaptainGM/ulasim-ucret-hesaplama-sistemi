package com.ulasim.hesaplama.model;

public class Baglanti {
    private final Durak hedefDurak;
    private final String aracTipi;
    private final double mesafe;
    private final double sure;
    private final double ucret;

    public Baglanti(Durak hedefDurak, String aracTipi, double mesafe, double sure, double ucret) {
        this.hedefDurak = hedefDurak;
        this.aracTipi = aracTipi;
        this.mesafe = mesafe;
        this.sure = sure;
        this.ucret = ucret;
    }

    public Durak getHedefDurak() {
        return hedefDurak;
    }

    public String getAracTipi() {
        return aracTipi;
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
