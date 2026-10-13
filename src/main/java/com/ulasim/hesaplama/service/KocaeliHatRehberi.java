package com.ulasim.hesaplama.service;

/**
 * Kocaeli Büyükşehir Belediyesi UlaşımPark resmi hat rehberi ve
 * toplu taşıma / taksi hat eşleştirme servisi.
 */
public final class KocaeliHatRehberi {

    private KocaeliHatRehberi() {
    }

    public static String temizDurakAdi(String isim) {
        if (isim == null) return "";
        return isim
                .replace("(Akçaray Tram)", "Tramvay")
                .replace("(Akçaray)", "Tramvay")
                .replace("(Tram)", "Tramvay")
                .replace("(Terminal)", "")
                .replace("(Merkez)", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    public static String hatBelirle(String tip, String aracTipi, String baslangicAdi, String bitisAdi) {
        String b = (baslangicAdi != null ? baslangicAdi.toLowerCase() : "");
        String h = (bitisAdi != null ? bitisAdi.toLowerCase() : "");
        String arac = (aracTipi != null ? aracTipi.toLowerCase() : (tip != null ? tip.toLowerCase() : ""));

        if ("tramvay".equals(arac)) {
            return "🚋 Tramvay";
        }
        if ("taksi".equals(arac)) {
            return "🚕 Sarı Taksi";
        }
        if ("yurume".equals(arac) || "transfer".equals(arac)) {
            return "🚶 Yaya Yolu";
        }

        // Kocaeli Üniversitesi Umuttepe Kampüsü Hatları
        if (b.contains("umuttepe") || h.contains("umuttepe")) {
            if (b.contains("otogar") || h.contains("otogar")) {
                return "🚌 Hat 210 (Otogar-Umuttepe Ekspres)";
            }
            if (b.contains("sekapark") || h.contains("sekapark")) {
                return "🚌 Hat 14 (Sekapark-Umuttepe)";
            }
            if (b.contains("hastane") || h.contains("hastane")) {
                return "🚌 Hat 33 (Şehir Hastanesi-Umuttepe)";
            }
            return "🚌 Hat 10 / 13 (Umuttepe Kampüs)";
        }

        // Kocaeli Şehir Hastanesi Hatları
        if (b.contains("şehir hastanesi") || h.contains("şehir hastanesi") || b.contains("sehir hastanesi") || h.contains("sehir hastanesi")) {
            if (b.contains("otogar") || h.contains("otogar")) {
                return "🚌 Hat SH1 (Otogar-Şehir Hastanesi)";
            }
            return "🚌 Hat 34 (Şehir Hastanesi Ekspres)";
        }

        // Gebze & Marmaray & Kartal Hatları
        if (b.contains("gebze") || h.contains("gebze")) {
            return "🚌 Hat 200 (İzmit-Gebze-Kartal Metro)";
        }

        // Gölcük & Karamürsel & Değirmendere Hatları
        if (b.contains("gölcük") || h.contains("gölcük") || b.contains("karamürsel") || h.contains("karamürsel") || b.contains("değirmendere") || h.contains("degirmendere")) {
            return "🚌 Hat 700 / 720 (Gölcük-Karamürsel)";
        }

        // Kartepe & Özdilek & Köseköy & Maşukiye Hatları
        if (b.contains("özdilek") || h.contains("ozdilek") || b.contains("kartepe") || h.contains("kartepe") || b.contains("köseköy") || h.contains("kosekoy") || b.contains("maşukiye") || h.contains("masukiye")) {
            return "🚌 Hat 211 / 212 (Kartepe-Özdilek)";
        }

        // Körfez & Hereke & Yarımca & Tütünçiftlik Hatları
        if (b.contains("körfez") || h.contains("korfez") || b.contains("hereke") || b.contains("yarımca") || b.contains("tütünçiftlik") || b.contains("derince")) {
            return "🚌 Hat 151 / 161 (Körfez Sahil)";
        }

        // Başiskele & Yuvacık Hatları
        if (b.contains("başiskele") || h.contains("basiskele") || b.contains("yuvacık") || h.contains("yuvacik")) {
            return "🚌 Hat 631 (Başiskele-Yuvacık)";
        }

        // Kandıra & Karadeniz Sahili Hatları
        if (b.contains("kandıra") || h.contains("kandira") || b.contains("kerpe") || h.contains("kerpe")) {
            return "🚌 Hat 800 (Kandıra-Kerpe Sahili)";
        }

        // Darıca & Çayırova & Dilovası Hatları
        if (b.contains("darıca") || h.contains("darica") || b.contains("çayırova") || h.contains("cayirova") || b.contains("dilovası") || h.contains("dilovasi")) {
            return "🚌 Hat 400 (Darıca-Çayırova-Gebze)";
        }

        // Yahya Kaptan Hatları
        if (b.contains("yahya kaptan") || h.contains("yahya kaptan")) {
            return "🚌 Hat 24 (Yahya Kaptan-Halkevi)";
        }

        // Otogar - Merkez / Halkevi Hatları
        if ((b.contains("otogar") && h.contains("halkevi")) || (b.contains("halkevi") && h.contains("otogar"))) {
            return "🚌 Hat 26 / 28 (Otogar-Çarşı)";
        }

        // Genel Kocaeli Belediye Otobüsü
        return "🚌 Belediye Otobüsü (UlaşımPark)";
    }
}
