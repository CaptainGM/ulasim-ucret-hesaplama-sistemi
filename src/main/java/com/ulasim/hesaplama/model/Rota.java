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

    private String rozet = "";
    private TrafikDurumu trafikDurumu = TrafikDurumu.NORMAL;
    private double co2SalinimiGram = 0;
    private double kaloriKcal = 0;
    private double tasarrufMiktari = 0;

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

        // CO2 Salınım Metriği (g/km)
        // Taksi: ~150g, Otobüs: ~35g, Tramvay: ~15g (elektrikli raylı), Yürüme: 0g
        double mesafe = segment.getMesafe();
        switch (segment.getTip()) {
            case "taksi" -> co2SalinimiGram += mesafe * 150.0;
            case "otobus" -> co2SalinimiGram += mesafe * 35.0;
            case "tramvay" -> co2SalinimiGram += mesafe * 15.0;
            case "yurume" -> kaloriKcal += mesafe * 55.0; // Yürüyüşte yakılan kalori
            default -> {}
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

    public String getRozet() {
        return rozet;
    }

    public void setRozet(String rozet) {
        this.rozet = rozet;
    }

    public TrafikDurumu getTrafikDurumu() {
        return trafikDurumu;
    }

    public void setTrafikDurumu(TrafikDurumu trafikDurumu) {
        this.trafikDurumu = trafikDurumu;
    }

    public double getCo2SalinimiGram() {
        return co2SalinimiGram;
    }

    public double getKaloriKcal() {
        return kaloriKcal;
    }

    public double getTasarrufMiktari() {
        return tasarrufMiktari;
    }

    public void setTasarrufMiktari(double tasarrufMiktari) {
        this.tasarrufMiktari = tasarrufMiktari;
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

