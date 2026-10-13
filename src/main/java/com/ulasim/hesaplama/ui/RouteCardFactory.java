package com.ulasim.hesaplama.ui;

import com.ulasim.hesaplama.domain.yolcu.Yolcu;
import com.ulasim.hesaplama.model.Rota;
import com.ulasim.hesaplama.model.RotaSegmenti;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

final class RouteCardFactory {
    private RouteCardFactory() {
    }

    static VBox olustur(Rota rota, Yolcu yolcu, Runnable haritadaGoster) {
        double toplamIndirimliUcret = 0;
        for (RotaSegmenti segment : rota.getSegmentler()) {
            toplamIndirimliUcret += yolcu.indirimHesapla(segment.getUcret(), segment.getAracTipi());
        }

        // 1. Başlık ve Rozetler
        Label baslik = new Label(rota.getRotaAdi());
        baslik.getStyleClass().add("route-title");

        HBox rozetKutusu = new HBox(6);
        rozetKutusu.setAlignment(Pos.CENTER_LEFT);

        if (rota.getRozet() != null && !rota.getRozet().isBlank()) {
            Label oncelikRozeti = new Label(rota.getRozet());
            oncelikRozeti.getStyleClass().addAll("route-badge", rozetStiliSec(rota.getRozet()));
            rozetKutusu.getChildren().add(oncelikRozeti);
        }

        Region baslikSpacer = new Region();
        HBox.setHgrow(baslikSpacer, Priority.ALWAYS);

        Label ucretRozeti = new Label(String.format("%.2f TL", toplamIndirimliUcret));
        ucretRozeti.getStyleClass().addAll("route-badge", "route-badge-price");

        HBox baslikSatiri = new HBox(8, baslik, rozetKutusu, baslikSpacer, ucretRozeti);
        baslikSatiri.setAlignment(Pos.CENTER_LEFT);

        // 2. Temel Metrikler (Süre, Mesafe, Aktarma)
        Label sureLabel = new Label(String.format("⏱ %.0f dk", rota.getToplamSure()));
        sureLabel.getStyleClass().add("metric-highlight");

        Label mesafeLabel = new Label(String.format("📍 %.2f km", rota.getToplamMesafe()));
        mesafeLabel.getStyleClass().add("segment-meta");

        Label aktarmaLabel = new Label(String.format("🔄 %d aktarma", rota.getAktarmaSayisi()));
        aktarmaLabel.getStyleClass().add("segment-meta");

        HBox metrikSatiri = new HBox(12, sureLabel, mesafeLabel, aktarmaLabel);
        metrikSatiri.setAlignment(Pos.CENTER_LEFT);

        // 3. Eko & Tasarruf Bilgisi (CO2 & Para Tasarrufu)
        HBox ekoSatiri = new HBox(8);
        ekoSatiri.setAlignment(Pos.CENTER_LEFT);

        if (rota.getTasarrufMiktari() > 1.0) {
            Label tasarrufRozeti = new Label(String.format("💰 %.1f TL Tasarruf", rota.getTasarrufMiktari()));
            tasarrufRozeti.getStyleClass().addAll("badge-pill", "badge-saving");
            ekoSatiri.getChildren().add(tasarrufRozeti);
        }

        if (rota.getCo2SalinimiGram() > 0) {
            Label co2Rozeti = new Label(String.format("🌿 %.0fg CO₂", rota.getCo2SalinimiGram()));
            co2Rozeti.getStyleClass().addAll("badge-pill", "badge-eco");
            ekoSatiri.getChildren().add(co2Rozeti);
        }

        if (rota.getKaloriKcal() > 5.0) {
            Label kaloriRozeti = new Label(String.format("🔥 %.0f kcal", rota.getKaloriKcal()));
            kaloriRozeti.getStyleClass().addAll("badge-pill", "badge-calorie");
            ekoSatiri.getChildren().add(kaloriRozeti);
        }

        // 4. Adım Adım Zaman Çizelgesi (Timeline / Stepper)
        VBox segmentKutusu = new VBox(6);
        segmentKutusu.getStyleClass().add("timeline-container");
        for (int i = 0; i < rota.getSegmentler().size(); i++) {
            RotaSegmenti segment = rota.getSegmentler().get(i);
            boolean sonMu = (i == rota.getSegmentler().size() - 1);
            segmentKutusu.getChildren().add(segmentTimelineSatiri(segment, yolcu, sonMu));
        }

        // 5. Alt Bilgi & Harita Odaklama Aksiyonu (Opensource UI Butonu)
        javafx.scene.control.Button odaklaBtn = new javafx.scene.control.Button("🗺️ Haritada Göster →");
        odaklaBtn.getStyleClass().add("btn-route-action");
        odaklaBtn.setOnAction(e -> haritadaGoster.run());

        Region altSpacer = new Region();
        HBox.setHgrow(altSpacer, Priority.ALWAYS);

        Label detayBilgi = new Label(rota.getSegmentler().size() + " Adım");
        detayBilgi.getStyleClass().add("segment-meta");

        HBox altSatir = new HBox(8, odaklaBtn, altSpacer, detayBilgi);
        altSatir.setAlignment(Pos.CENTER_LEFT);

        VBox kart = new VBox(10, baslikSatiri, metrikSatiri);
        if (!ekoSatiri.getChildren().isEmpty()) {
            kart.getChildren().add(ekoSatiri);
        }
        kart.getChildren().addAll(new Separator(), segmentKutusu, altSatir);
        kart.getStyleClass().add("route-card");
        kart.setOnMouseClicked(e -> haritadaGoster.run());

        return kart;
    }

    private static HBox segmentTimelineSatiri(RotaSegmenti segment, Yolcu yolcu, boolean sonMu) {
        // İkon çemberi
        Label ikonLabel = new Label(ikon(segment.getTip()));
        ikonLabel.getStyleClass().addAll("step-icon", "step-icon-" + segment.getTip());

        // Segment Açıklaması
        VBox detayKutusu = new VBox(3);
        String bIsim = temizDurakAdi(segment.getBaslangicDurak().isim);
        String hIsim = temizDurakAdi(segment.getBitisDurak().isim);
        Label durakAdlari = new Label(bIsim + " ➔ " + hIsim);
        durakAdlari.getStyleClass().add("step-title");

        double indirimliUcret = yolcu.indirimHesapla(segment.getUcret(), segment.getAracTipi());
        StringBuilder metaStr = new StringBuilder();
        metaStr.append(aracAciklamasi(segment.getTip(), segment.getAracTipi()));
        metaStr.append(String.format(" · %.1f km · %.0f dk", segment.getMesafe(), segment.getSure()));
        
        if (indirimliUcret > 0) {
            metaStr.append(String.format(" · %.2f TL", indirimliUcret));
        } else if (!segment.getTip().equals("yurume")) {
            metaStr.append(" · Ücretsiz Aktarma");
        }

        Label metaLabel = new Label(metaStr.toString());
        metaLabel.getStyleClass().add("step-meta");
        detayKutusu.getChildren().addAll(durakAdlari, metaLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox satir = new HBox(10, ikonLabel, detayKutusu, spacer);
        satir.setAlignment(Pos.CENTER_LEFT);
        satir.setPadding(new Insets(4, 0, 4, 0));
        return satir;
    }

    private static String temizDurakAdi(String isim) {
        if (isim == null) return "";
        return isim
                .replace("(Akçaray Tram)", "İstasyonu")
                .replace("(Akçaray)", "İstasyonu")
                .replace("(Terminal)", "")
                .replace("(Merkez)", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static String aracAciklamasi(String tip, String aracTipi) {
        if ("tramvay".equalsIgnoreCase(aracTipi) || "tramvay".equalsIgnoreCase(tip)) {
            return "Tramvay";
        }
        if ("otobus".equalsIgnoreCase(aracTipi) || "otobus".equalsIgnoreCase(tip)) {
            return "Belediye Otobüsü";
        }
        if ("taksi".equalsIgnoreCase(aracTipi) || "taksi".equalsIgnoreCase(tip)) {
            return "Sarı Taksi";
        }
        if ("transfer".equalsIgnoreCase(tip)) {
            return "İstasyon Aktarması";
        }
        return "Yürüyüş";
    }

    private static String rozetStiliSec(String rozet) {
        if (rozet.contains("Hızlı")) return "badge-fast";
        if (rozet.contains("Ekonomik")) return "badge-cheap";
        if (rozet.contains("Taksi")) return "badge-taxi";
        if (rozet.contains("Raylı")) return "badge-tram";
        return "badge-general";
    }

    private static String ikon(String tip) {
        return switch (tip) {
            case "yurume" -> "🚶";
            case "taksi" -> "🚖";
            case "otobus" -> "🚌";
            case "tramvay" -> "🚋";
            case "transfer" -> "🔄";
            default -> "📍";
        };
    }
}
