package com.ulasim.hesaplama.service;

import com.ulasim.hesaplama.distance.KonumYoneticisi;
import com.ulasim.hesaplama.domain.arac.Taksi;
import com.ulasim.hesaplama.domain.yolcu.Yolcu;
import com.ulasim.hesaplama.graph.Dijkstra;
import com.ulasim.hesaplama.graph.UlasimGrafi;
import com.ulasim.hesaplama.model.Durak;
import com.ulasim.hesaplama.model.Konum;
import com.ulasim.hesaplama.model.Rota;
import com.ulasim.hesaplama.model.RotaSegmenti;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UlasimSistemi {
    private static final double YURUME_HIZI = 5.0;
    private static final double MAKSIMUM_YURUME_MESAFESI = 3.0;

    private final UlasimGrafi graf = new UlasimGrafi();

    public UlasimSistemi(String classpathKaynagi) {
        try (InputStream in = getClass().getResourceAsStream(classpathKaynagi)) {
            if (in == null) {
                throw new IOException("Kaynak bulunamadı: " + classpathKaynagi);
            }
            yukleVeriler(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("Veri dosyası yüklenirken hata oluştu: " + e.getMessage());
        }
    }

    private void yukleVeriler(Reader reader) throws IOException {
        StringBuilder jsonContent = new StringBuilder();
        int c;
        while ((c = reader.read()) != -1) {
            jsonContent.append((char) c);
        }

        JSONObject jsonObject = new JSONObject(jsonContent.toString());
        JSONArray durakArray = jsonObject.getJSONArray("duraklar");

        for (int i = 0; i < durakArray.length(); i++) {
            JSONObject durakObj = durakArray.getJSONObject(i);

            String id = durakObj.getString("id");
            String name = durakObj.getString("name");
            String type = durakObj.getString("type");
            double lat = durakObj.getDouble("lat");
            double lon = durakObj.getDouble("lon");
            boolean sonDurak = durakObj.getBoolean("sonDurak");

            graf.durakEkle(new Durak(id, name, type, lat, lon, sonDurak));
        }

        for (int i = 0; i < durakArray.length(); i++) {
            JSONObject durakObj = durakArray.getJSONObject(i);
            String durakId = durakObj.getString("id");

            if (durakObj.has("nextStops") && !durakObj.isNull("nextStops")) {
                JSONArray nextStopsArray = durakObj.getJSONArray("nextStops");
                for (int j = 0; j < nextStopsArray.length(); j++) {
                    JSONObject nextStopObj = nextStopsArray.getJSONObject(j);

                    String stopId = nextStopObj.getString("stopId");
                    double mesafe = nextStopObj.getDouble("mesafe");
                    double sure = nextStopObj.getDouble("sure");
                    double ucret = nextStopObj.getDouble("ucret");

                    String aracTipi = graf.getDurak(durakId).tip.equals("bus") ? "otobus" : "tramvay";
                    graf.baglantiEkle(durakId, stopId, aracTipi, mesafe, sure, ucret);
                }
            }

            if (durakObj.has("transfer") && !durakObj.isNull("transfer")) {
                JSONObject transferObj = durakObj.getJSONObject("transfer");

                String transferStopId = transferObj.getString("transferStopId");
                double transferSure = transferObj.getDouble("transferSure");
                double transferUcret = transferObj.getDouble("transferUcret");

                graf.transferEkle(durakId, transferStopId, transferSure, transferUcret);
            }
        }

        if (jsonObject.has("rotaHatlari") && !jsonObject.isNull("rotaHatlari")) {
            JSONArray hatlarArray = jsonObject.getJSONArray("rotaHatlari");
            for (int i = 0; i < hatlarArray.length(); i++) {
                JSONObject hatObj = hatlarArray.getJSONObject(i);
                String hatAdi = hatObj.getString("hatAdi");
                JSONArray durakIdleriArray = hatObj.getJSONArray("durakIdleri");

                List<String> durakIdleri = new ArrayList<>();
                for (int j = 0; j < durakIdleriArray.length(); j++) {
                    durakIdleri.add(durakIdleriArray.getString(j));
                }

                graf.rotaHattiEkle(hatAdi, durakIdleri);
            }
        }
    }

    public List<Rota> rotaHesapla(Konum baslangic, Konum hedef, Yolcu yolcu) {
        List<Rota> rotalar = new ArrayList<>();

        hesaplaTaksiRotasi(baslangic, hedef, rotalar);
        hesaplaTopluTasimaRotasi(baslangic, hedef, rotalar);
        hesaplaEnKisaYolRotasi(baslangic, hedef, "sure", rotalar);
        hesaplaEnKisaYolRotasi(baslangic, hedef, "ucret", rotalar);

        Collections.sort(rotalar);

        return rotalar;
    }

    private void hesaplaTaksiRotasi(Konum baslangic, Konum hedef, List<Rota> rotalar) {
        Rota taksiRota = new Rota("Taksi ile Direkt Rota");
        double toplamMesafe = KonumYoneticisi.getInstance().mesafeHesapla(baslangic, hedef);
        Taksi taksi = new Taksi("TAX001");
        double taksiUcret = taksi.ucretHesapla(toplamMesafe);
        double taksiSure = taksi.sureHesapla(toplamMesafe);

        Durak baslangicDurak = new Durak("baslangic", "Başlangıç Noktası", "özel", baslangic.getEnlem(), baslangic.getBoylam(), false);
        Durak hedefDurak = new Durak("hedef", "Hedef Noktası", "özel", hedef.getEnlem(), hedef.getBoylam(), false);

        RotaSegmenti taksiSegmenti = new RotaSegmenti("taksi", baslangicDurak, hedefDurak, toplamMesafe, taksiSure, taksiUcret, null);
        taksiRota.ekleSegment(taksiSegmenti);

        rotalar.add(taksiRota);
    }

    private void hesaplaTopluTasimaRotasi(Konum baslangic, Konum hedef, List<Rota> rotalar) {
        Durak baslangicDurak = new Durak("baslangic", "Başlangıç Noktası", "özel", baslangic.getEnlem(), baslangic.getBoylam(), false);
        Durak hedefDurak = new Durak("hedef", "Hedef Noktası", "özel", hedef.getEnlem(), hedef.getBoylam(), false);

        Durak enYakinBaslangicDuragi = graf.enYakinDurakBul(baslangic);
        Durak enYakinHedefDuragi = graf.enYakinDurakBul(hedef);

        Rota topluTasimaRota = new Rota("Toplu Taşıma Rotası");

        ekleYurumeTaksiSegmenti(topluTasimaRota, baslangicDurak, enYakinBaslangicDuragi);

        Dijkstra dijkstra = new Dijkstra(graf);
        List<RotaSegmenti> topluTasimaYolu = dijkstra.enKisaYoluBul(enYakinBaslangicDuragi, enYakinHedefDuragi, "sure");
        for (RotaSegmenti segment : topluTasimaYolu) {
            topluTasimaRota.ekleSegment(segment);
        }

        ekleYurumeTaksiSegmenti(topluTasimaRota, enYakinHedefDuragi, hedefDurak);

        rotalar.add(topluTasimaRota);
    }

    private void hesaplaEnKisaYolRotasi(Konum baslangic, Konum hedef, String kriterTipi, List<Rota> rotalar) {
        String rotaAdi = kriterTipi.equals("ucret") ? "En Ekonomik Rota" : "En Hızlı Rota";

        Durak baslangicDurak = new Durak("baslangic", "Başlangıç Noktası", "özel", baslangic.getEnlem(), baslangic.getBoylam(), false);
        Durak hedefDurak = new Durak("hedef", "Hedef Noktası", "özel", hedef.getEnlem(), hedef.getBoylam(), false);

        Durak enYakinBaslangicDuragi = graf.enYakinDurakBul(baslangic);
        Durak enYakinHedefDuragi = graf.enYakinDurakBul(hedef);

        double baslangicMesafesi = KonumYoneticisi.getInstance().mesafeHesapla(baslangic, enYakinBaslangicDuragi.getKonum());
        double bitisMesafesi = KonumYoneticisi.getInstance().mesafeHesapla(hedef, enYakinHedefDuragi.getKonum());

        boolean baslangicYurunebilir = baslangicMesafesi <= MAKSIMUM_YURUME_MESAFESI;
        boolean bitisYurunebilir = bitisMesafesi <= MAKSIMUM_YURUME_MESAFESI;

        double taksiMesafe = KonumYoneticisi.getInstance().mesafeHesapla(baslangic, hedef);
        Taksi taksi = new Taksi("TAX001");
        double taksiSure = taksi.sureHesapla(taksiMesafe);
        double taksiUcret = taksi.ucretHesapla(taksiMesafe);

        Dijkstra dijkstra = new Dijkstra(graf);
        List<RotaSegmenti> topluTasimaYolu = dijkstra.enKisaYoluBul(enYakinBaslangicDuragi, enYakinHedefDuragi, kriterTipi);

        double topluTasimaUcret = 0;
        double topluTasimaSure = 0;
        for (RotaSegmenti segment : topluTasimaYolu) {
            topluTasimaUcret += segment.getUcret();
            topluTasimaSure += segment.getSure();
        }

        double baslangicUcret = baslangicYurunebilir ? 0 : taksi.ucretHesapla(baslangicMesafesi);
        double bitisUcret = bitisYurunebilir ? 0 : taksi.ucretHesapla(bitisMesafesi);

        double baslangicSure = baslangicYurunebilir ?
                (baslangicMesafesi / YURUME_HIZI) * 60 : taksi.sureHesapla(baslangicMesafesi);
        double bitisSure = bitisYurunebilir ?
                (bitisMesafesi / YURUME_HIZI) * 60 : taksi.sureHesapla(bitisMesafesi);

        topluTasimaUcret += baslangicUcret + bitisUcret;
        topluTasimaSure += baslangicSure + bitisSure;

        if (kriterTipi.equals("ucret") && taksiUcret <= topluTasimaUcret) {
            return;
        } else if (kriterTipi.equals("sure") && taksiSure <= topluTasimaSure) {
            return;
        }

        Rota enKisaRota = new Rota(rotaAdi);

        ekleYurumeTaksiSegmenti(enKisaRota, baslangicDurak, enYakinBaslangicDuragi);

        for (RotaSegmenti segment : topluTasimaYolu) {
            enKisaRota.ekleSegment(segment);
        }

        ekleYurumeTaksiSegmenti(enKisaRota, enYakinHedefDuragi, hedefDurak);

        rotalar.add(enKisaRota);
    }

    private void ekleYurumeTaksiSegmenti(Rota rota, Durak baslangic, Durak hedef) {
        double mesafe = KonumYoneticisi.getInstance().mesafeHesapla(baslangic.getKonum(), hedef.getKonum());

        if (mesafe <= MAKSIMUM_YURUME_MESAFESI) {
            double sure = (mesafe / YURUME_HIZI) * 60;
            rota.ekleSegment(new RotaSegmenti("yurume", baslangic, hedef, mesafe, sure, 0.0, "yurume"));
        } else {
            Taksi taksi = new Taksi("TAX001");
            double ucret = taksi.ucretHesapla(mesafe);
            double sure = taksi.sureHesapla(mesafe);
            rota.ekleSegment(new RotaSegmenti("taksi", baslangic, hedef, mesafe, sure, ucret, "taksi"));
        }
    }

    public UlasimGrafi getGraf() {
        return graf;
    }
}
