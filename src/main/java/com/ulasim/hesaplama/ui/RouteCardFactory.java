package com.ulasim.hesaplama.ui;

import com.ulasim.hesaplama.domain.yolcu.Yolcu;
import com.ulasim.hesaplama.model.Rota;
import com.ulasim.hesaplama.model.RotaSegmenti;
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

        Label baslik = new Label(rota.getRotaAdi());
        baslik.getStyleClass().add("route-title");

        Region baslikSpacer = new Region();
        HBox.setHgrow(baslikSpacer, Priority.ALWAYS);

        Label ucretRozeti = new Label(String.format("%.2f TL", toplamIndirimliUcret));
        ucretRozeti.getStyleClass().addAll("route-badge", "route-badge-price");

        HBox baslikSatiri = new HBox(8, baslik, baslikSpacer, ucretRozeti);
        baslikSatiri.setAlignment(Pos.CENTER_LEFT);

        Label metaRozeti = new Label(String.format("%.0f dk  ·  %.2f km  ·  %d aktarma",
                rota.getToplamSure(), rota.getToplamMesafe(), rota.getAktarmaSayisi()));
        metaRozeti.getStyleClass().add("segment-meta");

        VBox segmentKutusu = new VBox(2);
        for (RotaSegmenti segment : rota.getSegmentler()) {
            segmentKutusu.getChildren().add(segmentSatiri(segment, yolcu));
        }

        Label haritaLink = new Label("🗺 Haritada Göster");
        haritaLink.getStyleClass().add("segment-meta");

        VBox kart = new VBox(8, baslikSatiri, metaRozeti, new Separator(), segmentKutusu, haritaLink);
        kart.getStyleClass().add("route-card");
        kart.setOnMouseClicked(e -> haritadaGoster.run());
        return kart;
    }

    private static HBox segmentSatiri(RotaSegmenti segment, Yolcu yolcu) {
        String aciklama = ikon(segment.getTip()) + "  " + baslikMetni(segment.getTip()) + ": "
                + segment.getBaslangicDurak().isim + " → " + segment.getBitisDurak().isim;

        Label aciklamaEtiketi = new Label(aciklama);
        aciklamaEtiketi.getStyleClass().add("segment-row");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        double indirimliUcret = yolcu.indirimHesapla(segment.getUcret(), segment.getAracTipi());
        StringBuilder metaMetni = new StringBuilder(String.format("%.2f km · %.0f dk", segment.getMesafe(), segment.getSure()));
        if (indirimliUcret > 0) {
            metaMetni.append(String.format(" · %.2f TL", indirimliUcret));
        } else if (!segment.getTip().equals("yurume")) {
            metaMetni.append(" · Ücretsiz");
        }

        Label metaEtiketi = new Label(metaMetni.toString());
        metaEtiketi.getStyleClass().add("segment-meta");

        HBox satir = new HBox(8, aciklamaEtiketi, spacer, metaEtiketi);
        satir.setAlignment(Pos.CENTER_LEFT);
        return satir;
    }

    private static String ikon(String tip) {
        return switch (tip) {
            case "yurume" -> "🚶";
            case "taksi" -> "🚖";
            case "otobus" -> "🚌";
            case "tramvay" -> "🚋";
            case "transfer" -> "🔄";
            default -> "•";
        };
    }

    private static String baslikMetni(String tip) {
        return switch (tip) {
            case "yurume" -> "Yürüme";
            case "taksi" -> "Taksi";
            case "otobus" -> "Otobüs";
            case "tramvay" -> "Tramvay";
            case "transfer" -> "Transfer";
            default -> tip;
        };
    }
}
