package com.ulasim.hesaplama.model;

import java.util.ArrayList;
import java.util.List;

public class Rota implements Comparable<Rota> {
    private final List<RotaSegmenti> segmentler = new ArrayList<>();
    private double toplamUcret = 0;
    private double toplamMesafe = 0;
    private double toplamSure = 0;
    private int aktarmaSayisi = 0;
    private final String rotaAdi;

    public Rota(String rotaAdi) {
        this.rotaAdi = rotaAdi;
    }

    public void ekleSegment(RotaSegmenti segment) {
        segmentler.add(segment);
        toplamUcret += segment.getUcret();
        toplamMesafe += segment.getMesafe();
        toplamSure += segment.getSure();

        if (segment.getTip().equals("transfer")) {
            aktarmaSayisi++;
        }
    }

    public double getToplamUcret() {
        return toplamUcret;
    }

    public double getToplamMesafe() {
        return toplamMesafe;
    }

    public double getToplamSure() {
        return toplamSure;
    }

    public int getAktarmaSayisi() {
        return aktarmaSayisi;
    }

    public String getRotaAdi() {
        return rotaAdi;
    }

    public List<RotaSegmenti> getSegmentler() {
        return segmentler;
    }

    @Override
    public int compareTo(Rota digerRota) {
        int sureFarki = Double.compare(this.toplamSure, digerRota.toplamSure);
        if (sureFarki != 0) return sureFarki;

        int ucretFarki = Double.compare(this.toplamUcret, digerRota.toplamUcret);
        if (ucretFarki != 0) return ucretFarki;
        return Integer.compare(this.aktarmaSayisi, digerRota.aktarmaSayisi);
    }
}
