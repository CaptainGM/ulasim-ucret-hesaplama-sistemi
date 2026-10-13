package com.ulasim.hesaplama.model;

/**
 * Şehir içi ulaşımda anlık trafik koşullarını ve bunun araç türlerine
 * etkisini modelleyen durum enum'ı.
 *
 * Tramvay gibi raylı sistemler trafik katsayısından etkilenmezken,
 * otobüs ve taksi gibi karayolu araçlarının seyahat süreleri ve taksi
 * bekleme maliyetleri bu katsayılara göre dinamik olarak hesaplanır.
 */
public enum TrafikDurumu {
    NORMAL("Akıcı Trafik", 1.0, 1.0, "🟢", "Yollar açık, seferler standart sürelerde."),
    YOGUN("Yoğun Trafik (Pik Saat)", 1.45, 1.20, "🟡", "Otobüs & taksi gecikmeli; raylı sistem tam zamanında."),
    KILIT("Kilit / Yağışlı Trafik", 2.10, 1.45, "🔴", "Ağır sıkışıklık! Tramvay raylı sistem avantajı öne çıkar.");

    private final String baslik;
    private final double sureKatsayisi;
    private final double taksiMaliyetKatsayisi;
    private final String ikon;
    private final String aciklama;

    TrafikDurumu(String baslik, double sureKatsayisi, double taksiMaliyetKatsayisi, String ikon, String aciklama) {
        this.baslik = baslik;
        this.sureKatsayisi = sureKatsayisi;
        this.taksiMaliyetKatsayisi = taksiMaliyetKatsayisi;
        this.ikon = ikon;
        this.aciklama = aciklama;
    }

    public String getBaslik() {
        return baslik;
    }

    public double getSureKatsayisi() {
        return sureKatsayisi;
    }

    public double getTaksiMaliyetKatsayisi() {
        return taksiMaliyetKatsayisi;
    }

    public String getIkon() {
        return ikon;
    }

    public String getAciklama() {
        return aciklama;
    }

    /**
     * Anlık yerel saate göre kentsel trafik yoğunluğunu Google Haritalar
     * tarzında otomatik olarak tarar ve modeller.
     */
    public static TrafikDurumu anlikTaramayiGetir() {
        int saat = java.time.LocalTime.now().getHour();
        int dakika = java.time.LocalTime.now().getMinute();
        int toplamDakika = saat * 60 + dakika;

        // Sabah Pik Saati: 07:45 - 09:30
        if (toplamDakika >= 465 && toplamDakika <= 570) {
            return YOGUN;
        }
        // Akşam Pik Saati: 17:00 - 19:45 (İş çıkışı ve ana arter yoğunluğu)
        if (toplamDakika >= 1020 && toplamDakika <= 1185) {
            return YOGUN;
        }
        // Standart saatler
        return NORMAL;
    }

    @Override
    public String toString() {
        return ikon + " " + baslik;
    }
}

