package com.ulasim.hesaplama.graph;

import com.ulasim.hesaplama.distance.KonumYoneticisi;
import com.ulasim.hesaplama.model.Baglanti;
import com.ulasim.hesaplama.model.Durak;
import com.ulasim.hesaplama.model.Konum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UlasimGrafi {
    private final Map<String, Durak> duraklar = new HashMap<>();
    private final Map<String, List<String>> rotaHatlari = new HashMap<>();

    public void durakEkle(Durak durak) {
        duraklar.put(durak.id, durak);
    }

    public void baglantiEkle(String baslangicId, String hedefId, String aracTipi, double mesafe, double sure, double ucret) {
        Durak baslangicDurak = duraklar.get(baslangicId);
        Durak hedefDurak = duraklar.get(hedefId);

        if (baslangicDurak != null && hedefDurak != null) {
            Baglanti baglanti = new Baglanti(hedefDurak, aracTipi, mesafe, sure, ucret);
            baslangicDurak.baglantiEkle(baglanti);
        }
    }

    public void transferEkle(String durakId1, String durakId2, double sure, double ucret) {
        Durak durak1 = duraklar.get(durakId1);
        Durak durak2 = duraklar.get(durakId2);

        if (durak1 != null && durak2 != null) {
            double mesafe = KonumYoneticisi.getInstance().mesafeHesapla(durak1.getKonum(), durak2.getKonum());
            Baglanti transfer1 = new Baglanti(durak2, "transfer", mesafe, sure, ucret);
            Baglanti transfer2 = new Baglanti(durak1, "transfer", mesafe, sure, ucret);

            durak1.baglantiEkle(transfer1);
            durak2.baglantiEkle(transfer2);
        }
    }

    public Durak getDurak(String id) {
        return duraklar.get(id);
    }

    public Collection<Durak> getAllDuraklar() {
        return duraklar.values();
    }

    public void rotaHattiEkle(String hatAdi, List<String> durakIdleri) {
        rotaHatlari.put(hatAdi, new ArrayList<>(durakIdleri));
    }

    public Map<String, List<String>> getRotaHatlari() {
        return rotaHatlari;
    }

    public Durak enYakinDurakBul(Konum konum, String tip) {
        Durak enYakinDurak = null;
        double enKisaMesafe = Double.MAX_VALUE;

        for (Durak durak : duraklar.values()) {
            if (tip == null || durak.tip.equals(tip)) {
                double mesafe = KonumYoneticisi.getInstance().mesafeHesapla(konum, durak.getKonum());
                if (mesafe < enKisaMesafe) {
                    enKisaMesafe = mesafe;
                    enYakinDurak = durak;
                }
            }
        }

        return enYakinDurak;
    }

    public Durak enYakinDurakBul(Konum konum) {
        return enYakinDurakBul(konum, null);
    }
}
