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
import com.ulasim.hesaplama.model.TrafikDurumu;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Ulaşım ağı grafını yöneten, çok modlu (otobüs, tramvay, taksi, yürüme)
 * rota hesaplamalarını Dijkstra algoritması ve anlık trafik koşullarıyla
 * optimize eden ana servis katmanı.
 */
public class UlasimSistemi {
    private static final double YURUME_HIZI = 5.0; // km/s
    private static final double MAKSIMUM_YURUME_MESAFESI = 2.5; // km

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

        // 1. Durakları graf nesnesine ekle
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

        // 2. Hat bağlantılarını ekle (Çift yönlü hat desteği ile)
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
                    // Gerçek toplu taşıma şebekesinde araçlar çift yönlü çalışır
                    graf.baglantiEkle(stopId, durakId, aracTipi, mesafe, sure, ucret);
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

        // 3. Varsa özel rota hatlarını yükle
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
        return rotaHesapla(baslangic, hedef, yolcu, TrafikDurumu.NORMAL);
    }

    public List<Rota> rotaHesapla(Konum baslangic, Konum hedef, Yolcu yolcu, TrafikDurumu trafik) {
        List<Rota> rotalar = new ArrayList<>();
        TrafikDurumu aktifTrafik = (trafik != null) ? trafik : TrafikDurumu.NORMAL;

        // 1. Direkt Taksi Rotası
        double referansTaksiUcreti = hesaplaTaksiRotasi(baslangic, hedef, aktifTrafik, rotalar);

        // 2. Çok Modlu Toplu Taşıma Rotaları (Dijkstra)
        hesaplaTopluTasimaRotasi(baslangic, hedef, "sure", "En Hızlı Toplu Taşıma", aktifTrafik, rotalar);
        hesaplaTopluTasimaRotasi(baslangic, hedef, "ucret", "En Ekonomik Toplu Taşıma", aktifTrafik, rotalar);

        // Benzer/tekrar eden rotaları filtrele
        List<Rota> tekilRotalar = filtreleBenzerRotalar(rotalar);

        // Analitik değerleri ve rozetleri ata
        etiketleVeAnalizEt(tekilRotalar, referansTaksiUcreti, aktifTrafik);

        return tekilRotalar;
    }

    private double hesaplaTaksiRotasi(Konum baslangic, Konum hedef, TrafikDurumu trafik, List<Rota> rotalar) {
        Rota taksiRota = new Rota("Direkt Taksi");
        double toplamMesafe = KonumYoneticisi.getInstance().mesafeHesapla(baslangic, hedef);
        Taksi taksi = new Taksi("TAX001");
        double taksiUcret = taksi.ucretHesapla(toplamMesafe, trafik);
        double taksiSure = taksi.sureHesapla(toplamMesafe, trafik);

        Durak baslangicDurak = new Durak("baslangic", "Başlangıç Konumu", "özel", baslangic.getEnlem(), baslangic.getBoylam(), false);
        Durak hedefDurak = new Durak("hedef", "Varış Konumu", "özel", hedef.getEnlem(), hedef.getBoylam(), false);

        RotaSegmenti taksiSegmenti = new RotaSegmenti("taksi", baslangicDurak, hedefDurak, toplamMesafe, taksiSure, taksiUcret, "taksi");
        taksiRota.ekleSegment(taksiSegmenti);
        taksiRota.setRozet("🚖 Direkt Taksi");
        taksiRota.setTrafikDurumu(trafik);

        rotalar.add(taksiRota);
        return taksiUcret;
    }

    private void hesaplaTopluTasimaRotasi(Konum baslangic, Konum hedef, String kriterTipi, String rotaAdi, TrafikDurumu trafik, List<Rota> rotalar) {
        Durak enYakinBaslangicDuragi = graf.enYakinDurakBul(baslangic);
        Durak enYakinHedefDuragi = graf.enYakinDurakBul(hedef);

        if (enYakinBaslangicDuragi == null || enYakinHedefDuragi == null) {
            return;
        }

        Durak baslangicDurak = new Durak("baslangic", "Başlangıç Konumu", "özel", baslangic.getEnlem(), baslangic.getBoylam(), false);
        Durak hedefDurak = new Durak("hedef", "Varış Konumu", "özel", hedef.getEnlem(), hedef.getBoylam(), false);

        Dijkstra dijkstra = new Dijkstra(graf);
        List<RotaSegmenti> hamYol = dijkstra.enKisaYoluBul(enYakinBaslangicDuragi, enYakinHedefDuragi, kriterTipi);

        if (hamYol.isEmpty() && !enYakinBaslangicDuragi.equals(enYakinHedefDuragi)) {
            return;
        }

        // Ardışık durakları tek bir toplu taşıma etabında birleştir ve tek bilet ücreti uygula
        List<RotaSegmenti> topluTasimaYolu = birlestirTopluTasimaSegmentleri(hamYol);

        Rota rota = new Rota(rotaAdi);

        // Başlangıç noktasından ilk durağa ulaşım (Yürüme veya Taksi)
        ekleYurumeTaksiSegmenti(rota, baslangicDurak, enYakinBaslangicDuragi, trafik);

        // İlk aracı durakta bekleme süresi (Headway): Otobüs ~5 dk, Tramvay ~3 dk
        boolean ilkAracEklendi = false;

        // Toplu taşıma hattı segmentleri (Trafik katsayısı uygulanır)
        for (RotaSegmenti segment : topluTasimaYolu) {
            double duzeltilmisSure = segment.getSure();
            if ("otobus".equalsIgnoreCase(segment.getAracTipi())) {
                duzeltilmisSure = segment.getSure() * trafik.getSureKatsayisi();
                if (!ilkAracEklendi) {
                    duzeltilmisSure += 3.0; // İlk binişte ortalama durak bekleme
                    ilkAracEklendi = true;
                }
            } else if ("tramvay".equalsIgnoreCase(segment.getAracTipi())) {
                if (!ilkAracEklendi) {
                    duzeltilmisSure += 2.0; // Tramvay istasyon peron bekleme
                    ilkAracEklendi = true;
                }
            } else if ("transfer".equalsIgnoreCase(segment.getTip())) {
                duzeltilmisSure += 1.5; // Aktarma bekleme süresi payı
            }

            RotaSegmenti duzeltilmisSegment = new RotaSegmenti(
                    segment.getTip(),
                    segment.getBaslangicDurak(),
                    segment.getBitisDurak(),
                    segment.getMesafe(),
                    Math.round(duzeltilmisSure * 10.0) / 10.0,
                    segment.getUcret(),
                    segment.getAracTipi()
            );
            rota.ekleSegment(duzeltilmisSegment);
        }

        // Son duraktan hedef noktasına ulaşım
        ekleYurumeTaksiSegmenti(rota, enYakinHedefDuragi, hedefDurak, trafik);
        rota.setTrafikDurumu(trafik);

        rotalar.add(rota);
    }

    /**
     * Aynı hat ve araçtaki ardışık durakları (örneğin 5 durak boyunca tramvay)
     * tek bir kullanıcı dostu etaba birleştirir ve durak başı yerine TEK BİNİŞ ÜCRETİ uygular.
     */
    private List<RotaSegmenti> birlestirTopluTasimaSegmentleri(List<RotaSegmenti> hamSegmentler) {
        if (hamSegmentler == null || hamSegmentler.isEmpty()) {
            return Collections.emptyList();
        }

        List<RotaSegmenti> sonuc = new ArrayList<>();
        boolean ilkBiletAlindi = false;

        Durak baslangicAtlamaDuragi = null;

        int i = 0;
        while (i < hamSegmentler.size()) {
            RotaSegmenti baslangicSeg = hamSegmentler.get(i);
            String tip = baslangicSeg.getTip();
            String arac = baslangicSeg.getAracTipi();

            // Transfer / Yürüme segmentleri (0 TL)
            if ("yurume".equalsIgnoreCase(tip) || "transfer".equalsIgnoreCase(tip)) {
                if (baslangicSeg.getMesafe() < 0.05) {
                    // Aynı peron/terminal içi ihmal edilebilir aktarma (< 50m)
                    if (baslangicAtlamaDuragi == null) {
                        baslangicAtlamaDuragi = baslangicSeg.getBaslangicDurak();
                    }
                    i++;
                    continue;
                }
                sonuc.add(new RotaSegmenti(
                        tip,
                        baslangicSeg.getBaslangicDurak(),
                        baslangicSeg.getBitisDurak(),
                        baslangicSeg.getMesafe(),
                        baslangicSeg.getSure(),
                        0.0, // Transfer yürümesi ücretsizdir
                        tip
                ));
                i++;
                continue;
            }

            // Ardışık aynı aracı birleştir
            Durak ilkDurak = (baslangicAtlamaDuragi != null) ? baslangicAtlamaDuragi : baslangicSeg.getBaslangicDurak();
            baslangicAtlamaDuragi = null;
            Durak sonDurak = baslangicSeg.getBitisDurak();
            double toplamMesafe = baslangicSeg.getMesafe();
            double toplamSure = baslangicSeg.getSure();

            // İlk binişte tam tarife (Tramvay 36 TL, Otobüs 42 TL), aktarmada indirimli (12 TL)
            double biletUcreti;
            if (!ilkBiletAlindi) {
                biletUcreti = "tramvay".equalsIgnoreCase(arac) ? 36.0 : 42.0;
                ilkBiletAlindi = true;
            } else {
                biletUcreti = 12.0; // Kocaeli KentKart UKOME aktarma ücreti
            }

            int j = i + 1;
            while (j < hamSegmentler.size()) {
                RotaSegmenti sonraki = hamSegmentler.get(j);
                if (sonraki.getAracTipi().equalsIgnoreCase(arac) &&
                    !"transfer".equalsIgnoreCase(sonraki.getTip()) &&
                    !"yurume".equalsIgnoreCase(sonraki.getTip())) {
                    toplamMesafe += sonraki.getMesafe();
                    toplamSure += sonraki.getSure();
                    sonDurak = sonraki.getBitisDurak();
                    j++;
                } else {
                    break;
                }
            }

            sonuc.add(new RotaSegmenti(
                    tip,
                    ilkDurak,
                    sonDurak,
                    Math.round(toplamMesafe * 100.0) / 100.0,
                    Math.round(toplamSure * 10.0) / 10.0,
                    biletUcreti, // Durak sayısından bağımsız TEK BİNİŞ ÜCRETİ
                    arac
            ));

            i = j;
        }

        return sonuc;
    }

    private void ekleYurumeTaksiSegmenti(Rota rota, Durak baslangic, Durak hedef, TrafikDurumu trafik) {
        double mesafe = KonumYoneticisi.getInstance().mesafeHesapla(baslangic.getKonum(), hedef.getKonum());
        if (mesafe < 0.05) {
            return; // Çok yakınsa segment ekleme
        }

        if (mesafe <= MAKSIMUM_YURUME_MESAFESI) {
            // Yaya yürüyüşü: 4.8 km/s + kavşak/ışık bekleme payı
            double sure = (mesafe / 4.8) * 60 + (mesafe > 0.4 ? 1.5 : 0.5);
            rota.ekleSegment(new RotaSegmenti("yurume", baslangic, hedef, mesafe, Math.round(sure * 10.0) / 10.0, 0.0, "yurume"));
        } else {
            Taksi taksi = new Taksi("TAX001");
            double ucret = taksi.ucretHesapla(mesafe, trafik);
            double sure = taksi.sureHesapla(mesafe, trafik);
            rota.ekleSegment(new RotaSegmenti("taksi", baslangic, hedef, mesafe, Math.round(sure * 10.0) / 10.0, ucret, "taksi"));
        }
    }

    private List<Rota> filtreleBenzerRotalar(List<Rota> rotalar) {
        List<Rota> sonuc = new ArrayList<>();
        for (Rota r : rotalar) {
            boolean duplicate = false;
            for (Rota mevcut : sonuc) {
                if (Math.abs(mevcut.getToplamSure() - r.getToplamSure()) < 0.5 &&
                    Math.abs(mevcut.getToplamUcret() - r.getToplamUcret()) < 0.5 &&
                    mevcut.getSegmentler().size() == r.getSegmentler().size()) {
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate) {
                sonuc.add(r);
            }
        }
        return sonuc;
    }

    private void etiketleVeAnalizEt(List<Rota> rotalar, double referansTaksiUcreti, TrafikDurumu trafik) {
        if (rotalar.isEmpty()) return;

        // Süreye göre sırala
        rotalar.sort(Comparator.comparingDouble(Rota::getToplamSure));

        Rota enHizli = null;
        Rota enEkonomik = null;
        double minSure = Double.MAX_VALUE;
        double minUcret = Double.MAX_VALUE;

        for (Rota r : rotalar) {
            r.setTasarrufMiktari(Math.max(0, referansTaksiUcreti - r.getToplamUcret()));
            if (!r.getRotaAdi().contains("Taksi")) {
                if (r.getToplamSure() < minSure) {
                    minSure = r.getToplamSure();
                    enHizli = r;
                }
                if (r.getToplamUcret() < minUcret) {
                    minUcret = r.getToplamUcret();
                    enEkonomik = r;
                }
            }
        }

        for (Rota r : rotalar) {
            if (r.getRotaAdi().contains("Taksi")) {
                r.setRozet("🚖 Direkt Taksi");
            } else if (r == enHizli && r == enEkonomik) {
                r.setRozet("⭐ En Uygun & En Hızlı");
            } else if (r == enHizli) {
                r.setRozet("⚡ En Hızlı");
            } else if (r == enEkonomik) {
                r.setRozet("💰 En Ekonomik");
            } else {
                // Segmentlerinde tramvay var mı?
                boolean tramvayVar = r.getSegmentler().stream()
                        .anyMatch(s -> "tramvay".equalsIgnoreCase(s.getAracTipi()));
                if (tramvayVar) {
                    r.setRozet("🚊 Raylı Sistem (Trafiksiz)");
                } else {
                    r.setRozet("🌿 Toplu Taşıma");
                }
            }
        }
    }

    public UlasimGrafi getGraf() {
        return graf;
    }
}
