package com.ulasim.hesaplama.graph;

import com.ulasim.hesaplama.model.Baglanti;
import com.ulasim.hesaplama.model.Durak;
import com.ulasim.hesaplama.model.RotaSegmenti;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Dijkstra {
    private final UlasimGrafi graf;
    private Map<Durak, Double> uzakliklar;
    private Map<Durak, Durak> oncekiDuraklar;
    private Map<Durak, Baglanti> oncekilereGidenBaglantilar;
    private Set<Durak> ziyaretEdilmeyenDuraklar;

    public Dijkstra(UlasimGrafi graf) {
        this.graf = graf;
    }

    public List<RotaSegmenti> enKisaYoluBul(Durak baslangic, Durak hedef, String kriterTipi) {
        uzakliklar = new HashMap<>();
        oncekiDuraklar = new HashMap<>();
        oncekilereGidenBaglantilar = new HashMap<>();
        ziyaretEdilmeyenDuraklar = new HashSet<>();

        for (Durak durak : graf.getAllDuraklar()) {
            uzakliklar.put(durak, Double.MAX_VALUE);
            ziyaretEdilmeyenDuraklar.add(durak);
        }

        uzakliklar.put(baslangic, 0.0);

        while (!ziyaretEdilmeyenDuraklar.isEmpty()) {
            Durak suAnkiDurak = getEnKisaUzaklikliDurak();

            if (suAnkiDurak == null || suAnkiDurak.equals(hedef)) {
                break;
            }

            ziyaretEdilmeyenDuraklar.remove(suAnkiDurak);

            for (Baglanti baglanti : suAnkiDurak.getBaglantilar()) {
                Durak komsuDurak = baglanti.getHedefDurak();

                double deger;
                if ("ucret".equals(kriterTipi)) {
                    deger = baglanti.getUcret();
                } else if ("mesafe".equals(kriterTipi)) {
                    deger = baglanti.getMesafe();
                } else {
                    deger = baglanti.getSure();
                }

                double yeniUzaklik = uzakliklar.get(suAnkiDurak) + deger;

                if (yeniUzaklik < uzakliklar.get(komsuDurak)) {
                    uzakliklar.put(komsuDurak, yeniUzaklik);
                    oncekiDuraklar.put(komsuDurak, suAnkiDurak);
                    oncekilereGidenBaglantilar.put(komsuDurak, baglanti);
                }
            }
        }

        return yoluOlustur(baslangic, hedef);
    }

    private Durak getEnKisaUzaklikliDurak() {
        Durak enYakinDurak = null;
        double enKisaUzaklik = Double.MAX_VALUE;

        for (Durak durak : ziyaretEdilmeyenDuraklar) {
            double durakUzakligi = uzakliklar.get(durak);
            if (durakUzakligi < enKisaUzaklik) {
                enKisaUzaklik = durakUzakligi;
                enYakinDurak = durak;
            }
        }

        return enYakinDurak;
    }

    private List<RotaSegmenti> yoluOlustur(Durak baslangic, Durak hedef) {
        List<RotaSegmenti> yol = new ArrayList<>();
        Durak suAnkiDurak = hedef;

        if (oncekiDuraklar.get(hedef) == null) {
            return yol;
        }

        while (!suAnkiDurak.equals(baslangic)) {
            Durak oncekiDurak = oncekiDuraklar.get(suAnkiDurak);
            Baglanti baglanti = oncekilereGidenBaglantilar.get(suAnkiDurak);

            RotaSegmenti segment = new RotaSegmenti(
                    baglanti.getAracTipi(),
                    oncekiDurak,
                    suAnkiDurak,
                    baglanti.getMesafe(),
                    baglanti.getSure(),
                    baglanti.getUcret(),
                    baglanti.getAracTipi()
            );

            yol.add(0, segment);
            suAnkiDurak = oncekiDurak;
        }

        return yol;
    }
}
