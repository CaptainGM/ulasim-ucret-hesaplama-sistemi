package com.ulasim.hesaplama.ui;

import com.ulasim.hesaplama.domain.yolcu.GenelYolcu;
import com.ulasim.hesaplama.domain.yolcu.Ogrenci;
import com.ulasim.hesaplama.domain.yolcu.Yasli;
import com.ulasim.hesaplama.domain.yolcu.Yolcu;
import com.ulasim.hesaplama.model.Konum;
import com.ulasim.hesaplama.model.Rota;
import com.ulasim.hesaplama.model.RotaSegmenti;
import com.ulasim.hesaplama.payment.KentKart;
import com.ulasim.hesaplama.payment.KrediKarti;
import com.ulasim.hesaplama.payment.Nakit;
import com.ulasim.hesaplama.payment.OdemeStratejisi;
import com.ulasim.hesaplama.service.Geocoder;
import com.ulasim.hesaplama.service.UlasimSistemi;
import com.ulasim.hesaplama.ui.map.MapView;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Locale;

public class MainView extends BorderPane {
    private final UlasimSistemi sistem;

    private final TextField baslangicAramaField = new TextField();
    private final TextField hedefAramaField = new TextField();
    private final TextField baslangicLatField = new TextField("40.7625");
    private final TextField baslangicLonField = new TextField("30.4035");
    private final TextField hedefLatField = new TextField("40.7738");
    private final TextField hedefLonField = new TextField("30.4252");
    private final ComboBox<String> yolcuTipiBox = new ComboBox<>();
    private final ComboBox<String> odemeYontemiBox = new ComboBox<>();

    private final ToggleButton secBaslangicToggle = new ToggleButton("📍 Haritadan Seç");
    private final ToggleButton secHedefToggle = new ToggleButton("🎯 Haritadan Seç");
    private final ToggleGroup pickGroup = new ToggleGroup();

    private final Geocoder geocoder = new Geocoder();
    private final MapView mapView;
    private final VBox sonucKutusu = new VBox(12);
    private final Label yolcuBilgiEtiketi = new Label();

    public MainView(UlasimSistemi sistem) {
        this.sistem = sistem;
        this.mapView = new MapView(sistem.getGraf().getAllDuraklar(), this::haritayaTiklandi);

        setTop(formKarti());
        setCenter(ortaBolum());
        setPadding(new Insets(14));
    }

    private VBox formKarti() {
        yolcuTipiBox.getItems().addAll("Genel", "Öğrenci", "Yaşlı");
        yolcuTipiBox.getSelectionModel().selectFirst();

        odemeYontemiBox.getItems().addAll("KentKart", "Kredi Kartı", "Nakit");
        odemeYontemiBox.getSelectionModel().selectFirst();

        baslangicLatField.setPrefWidth(95);
        baslangicLonField.setPrefWidth(95);
        hedefLatField.setPrefWidth(95);
        hedefLonField.setPrefWidth(95);

        secBaslangicToggle.setToggleGroup(pickGroup);
        secHedefToggle.setToggleGroup(pickGroup);
        secBaslangicToggle.getStyleClass().add("button-toggle");
        secHedefToggle.getStyleClass().add("button-toggle");

        HBox baslangicSatiri = noktaSatiri("📍", "Başlangıç", baslangicAramaField, baslangicLatField, baslangicLonField, secBaslangicToggle);
        HBox hedefSatiri = noktaSatiri("🎯", "Hedef", hedefAramaField, hedefLatField, hedefLonField, secHedefToggle);

        Button hesaplaButton = new Button("Rota Hesapla");
        hesaplaButton.getStyleClass().add("button-primary");
        hesaplaButton.setOnAction(e -> rotaHesaplaButonuTiklandi());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox secenekSatiri = new HBox(12,
                alanli("Yolcu Tipi", yolcuTipiBox),
                alanli("Ödeme Yöntemi", odemeYontemiBox),
                spacer,
                hesaplaButton);
        secenekSatiri.setAlignment(Pos.BOTTOM_LEFT);

        VBox kart = new VBox(12, new Label("Ulaşım Sistemi") {{ getStyleClass().add("app-title"); }},
                baslangicSatiri, hedefSatiri, secenekSatiri);
        kart.getStyleClass().add("form-card");
        return kart;
    }

    private HBox noktaSatiri(String ikon, String etiket, TextField aramaField, TextField latField, TextField lonField, ToggleButton haritaToggle) {
        Label baslik = new Label(ikon + " " + etiket);
        baslik.getStyleClass().add("field-label");
        baslik.setMinWidth(80);

        aramaField.setPromptText(etiket + " için adres veya yer adı yazın...");
        aramaField.setPrefWidth(260);
        HBox.setHgrow(aramaField, Priority.ALWAYS);

        Button araButon = new Button("Ara");
        araButon.getStyleClass().add("button-toggle");
        Runnable arama = () -> adresAra(aramaField, latField, lonField, araButon);
        araButon.setOnAction(e -> arama.run());
        aramaField.setOnAction(e -> arama.run());

        latField.setPromptText("Enlem");
        lonField.setPromptText("Boylam");

        HBox satir = new HBox(8, baslik, aramaField, araButon, latField, lonField, haritaToggle);
        satir.setAlignment(Pos.CENTER_LEFT);
        return satir;
    }

    private void adresAra(TextField aramaField, TextField latField, TextField lonField, Button araButon) {
        String sorgu = aramaField.getText().trim();
        if (sorgu.isEmpty()) return;

        aramaField.getStyleClass().remove("field-error");
        araButon.setDisable(true);
        araButon.setText("...");

        Task<List<Geocoder.GeocodeSonuc>> gorev = new Task<>() {
            @Override
            protected List<Geocoder.GeocodeSonuc> call() throws Exception {
                return geocoder.ara(sorgu);
            }
        };

        gorev.setOnSucceeded(e -> {
            araButon.setDisable(false);
            araButon.setText("Ara");
            List<Geocoder.GeocodeSonuc> sonuclar = gorev.getValue();
            if (sonuclar.isEmpty()) {
                aramaField.getStyleClass().add("field-error");
                return;
            }
            if (sonuclar.size() == 1) {
                sonucSec(sonuclar.get(0), aramaField, latField, lonField);
                return;
            }
            ContextMenu menu = new ContextMenu();
            for (Geocoder.GeocodeSonuc sonuc : sonuclar) {
                MenuItem item = new MenuItem(sonuc.isim());
                item.setOnAction(ev -> sonucSec(sonuc, aramaField, latField, lonField));
                menu.getItems().add(item);
            }
            menu.show(aramaField, Side.BOTTOM, 0, 0);
        });

        gorev.setOnFailed(e -> {
            araButon.setDisable(false);
            araButon.setText("Ara");
            aramaField.getStyleClass().add("field-error");
        });

        Thread thread = new Thread(gorev, "adres-arama");
        thread.setDaemon(true);
        thread.start();
    }

    private void sonucSec(Geocoder.GeocodeSonuc sonuc, TextField aramaField, TextField latField, TextField lonField) {
        latField.setText(fmt6(sonuc.lat()));
        lonField.setText(fmt6(sonuc.lon()));
        String kisaAd = sonuc.isim().split(",")[0];
        aramaField.setText(kisaAd);
        haritaMarkerlariniGuncelle();
    }

    private void haritaMarkerlariniGuncelle() {
        Double bLat = parseOrNull(baslangicLatField);
        Double bLon = parseOrNull(baslangicLonField);
        Double hLat = parseOrNull(hedefLatField);
        Double hLon = parseOrNull(hedefLonField);
        if (bLat != null && bLon != null && hLat != null && hLon != null) {
            mapView.setMarkers(bLat, bLon, hLat, hLon);
        }
    }

    private VBox alanli(String etiket, javafx.scene.Node kontrol) {
        Label label = new Label(etiket);
        label.getStyleClass().add("field-label");
        VBox box = new VBox(4, label, kontrol);
        return box;
    }

    private SplitPane ortaBolum() {
        ScrollPane sonucScroll = new ScrollPane(sonucKutusu);
        sonucScroll.setFitToWidth(true);
        sonucScroll.setPadding(new Insets(0, 0, 0, 4));

        yolcuBilgiEtiketi.getStyleClass().add("segment-meta");
        sonucKutusu.getChildren().add(bosDurumIpucu());
        sonucKutusu.setPadding(new Insets(4));

        VBox sagPanel = new VBox(8, yolcuBilgiEtiketi, sonucScroll);
        VBox.setVgrow(sonucScroll, Priority.ALWAYS);
        sagPanel.setMinWidth(320);

        SplitPane splitPane = new SplitPane(mapView.getNode(), sagPanel);
        Platform.runLater(() -> splitPane.setDividerPositions(0.62));
        return splitPane;
    }

    private Label bosDurumIpucu() {
        Label ipucu = new Label("Koordinatları girip veya haritadan seçip \"Rota Hesapla\"ya basın.");
        ipucu.getStyleClass().add("empty-hint");
        return ipucu;
    }

    private void haritayaTiklandi(double lat, double lon) {
        if (secBaslangicToggle.isSelected()) {
            baslangicLatField.setText(fmt6(lat));
            baslangicLonField.setText(fmt6(lon));
        } else if (secHedefToggle.isSelected()) {
            hedefLatField.setText(fmt6(lat));
            hedefLonField.setText(fmt6(lon));
        } else {
            return;
        }

        pickGroup.selectToggle(null);
        haritaMarkerlariniGuncelle();
    }

    private void rotaHesaplaButonuTiklandi() {
        try {
            double baslangicLat = Double.parseDouble(baslangicLatField.getText().trim());
            double baslangicLon = Double.parseDouble(baslangicLonField.getText().trim());
            double hedefLat = Double.parseDouble(hedefLatField.getText().trim());
            double hedefLon = Double.parseDouble(hedefLonField.getText().trim());

            Konum baslangic = new Konum(baslangicLat, baslangicLon);
            Konum hedef = new Konum(hedefLat, hedefLon);
            Yolcu yolcu = yolcuOlustur();

            List<Rota> rotalar = sistem.rotaHesapla(baslangic, hedef, yolcu);

            mapView.setMarkers(baslangicLat, baslangicLon, hedefLat, hedefLon);
            mapView.clearRoute();
            sonucGoster(rotalar, yolcu);
        } catch (NumberFormatException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Lütfen geçerli koordinat değerleri girin!");
            alert.setHeaderText(null);
            alert.showAndWait();
        }
    }

    private Yolcu yolcuOlustur() {
        String yolcuTipi = yolcuTipiBox.getValue();
        OdemeStratejisi odeme = odemeStratejisiOlustur();

        return switch (yolcuTipi) {
            case "Öğrenci" -> new Ogrenci("Kullanıcı", odeme);
            case "Yaşlı" -> new Yasli("Kullanıcı", odeme);
            default -> new GenelYolcu("Kullanıcı", odeme);
        };
    }

    private OdemeStratejisi odemeStratejisiOlustur() {
        String odemeYontemi = odemeYontemiBox.getValue();

        return switch (odemeYontemi) {
            case "KentKart" -> new KentKart("12345", 100.0);
            case "Kredi Kartı" -> new KrediKarti("1234-5678-9101-1121", 1000.0);
            default -> new Nakit(50.0);
        };
    }

    private void sonucGoster(List<Rota> rotalar, Yolcu yolcu) {
        yolcuBilgiEtiketi.setText(String.format("Yolcu: %s (%s)  ·  Ödeme: %s (Bakiye: %.2f TL)",
                yolcu.isim, yolcu.tip, yolcu.odeme.getOdemeTipi(), yolcu.odeme.getBakiye()));

        sonucKutusu.getChildren().clear();
        if (rotalar.isEmpty()) {
            sonucKutusu.getChildren().add(bosDurumIpucu());
            return;
        }

        for (Rota rota : rotalar) {
            sonucKutusu.getChildren().add(RouteCardFactory.olustur(rota, yolcu, () -> mapView.drawRoute(rota)));
        }
    }

    private Double parseOrNull(TextField field) {
        try {
            return Double.parseDouble(field.getText().trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static String fmt6(double v) {
        return String.format(Locale.ROOT, "%.6f", v);
    }
}
