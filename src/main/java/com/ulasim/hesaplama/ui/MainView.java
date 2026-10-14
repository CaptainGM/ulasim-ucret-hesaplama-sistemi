package com.ulasim.hesaplama.ui;

import com.ulasim.hesaplama.domain.yolcu.GenelYolcu;
import com.ulasim.hesaplama.domain.yolcu.Ogrenci;
import com.ulasim.hesaplama.domain.yolcu.Yasli;
import com.ulasim.hesaplama.domain.yolcu.Yolcu;
import com.ulasim.hesaplama.model.Durak;
import com.ulasim.hesaplama.model.Konum;
import com.ulasim.hesaplama.model.Rota;
import com.ulasim.hesaplama.model.TrafikDurumu;
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

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Modern kent içi ulaşım ve çok modlu rota optimizasyon dashboard arayüzü.
 */
public class MainView extends BorderPane {
    public record PopulerKonum(String ad, double lat, double lon) {}

    public static final List<PopulerKonum> POPULER_KONUMLAR = List.of(
            // İzmit Merkez & Üniversite
            new PopulerKonum("İzmit Otogarı (Terminal)", 40.775970, 29.974113),
            new PopulerKonum("KOÜ Umuttepe Kampüsü", 40.824562, 29.921665),
            new PopulerKonum("Kocaeli Şehir Hastanesi", 40.782254, 29.957500),
            new PopulerKonum("Halkevi (İzmit Merkez)", 40.761963, 29.925769),
            new PopulerKonum("Sekapark Kültür Alanı", 40.761409, 29.910890),
            new PopulerKonum("Yahya Kaptan (Merkez)", 40.767487, 29.968779),
            new PopulerKonum("Doğu Kışla Gençlik Parkı", 40.768000, 29.953000),
            new PopulerKonum("KOÜ Anıtpark Yerleşkesi", 40.767500, 29.940500),
            new PopulerKonum("Plajyolu Sahil", 40.760088, 29.881773),
            new PopulerKonum("Kuruçeşme (Merkez)", 40.765956, 29.874524),
            new PopulerKonum("Kocaeli Stadyumu (Alikahya)", 40.775084, 30.017023),
            new PopulerKonum("Yuvam Akarca Konutları", 40.773813, 29.980894),

            // Alışveriş Merkezleri (AVM)
            new PopulerKonum("Symbol AVM & Medikal", 40.757872, 29.979561),
            new PopulerKonum("41 Burda AVM", 40.756627, 29.948176),
            new PopulerKonum("Outlet Center & Fuar", 40.754533, 29.945852),
            new PopulerKonum("Özdilek AVM (Kartepe)", 40.744871, 30.062017),
            new PopulerKonum("Gebze Center AVM", 40.798500, 29.439000),

            // Kartepe & Köseköy Bölgesi
            new PopulerKonum("Köseköy Merkez (Kartepe)", 40.748898, 30.006211),
            new PopulerKonum("Kartepe Kent Meydanı", 40.744980, 30.017614),
            new PopulerKonum("Maşukiye Doğa Parkı", 40.701930, 30.139800),
            new PopulerKonum("Kartepe Kayak & Zirve", 40.655000, 30.098000),

            // Başiskele & Gölcük & Karamürsel Hattı
            new PopulerKonum("Başiskele Sahil Parkı", 40.715928, 29.919298),
            new PopulerKonum("Yuvacık Barajı Girişi", 40.675353, 29.969197),
            new PopulerKonum("Gölcük Anıtpark Meydanı", 40.717500, 29.822500),
            new PopulerKonum("Gölcük Donanma Kapısı", 40.722000, 29.831000),
            new PopulerKonum("Değirmendere Çınarlık", 40.718000, 29.784000),
            new PopulerKonum("Halıdere Sahil", 40.716966, 29.757990),
            new PopulerKonum("Karamürsel Sahil Kordonu", 40.693500, 29.617000),

            // Derince & Körfez Bölgesi
            new PopulerKonum("Derince Kent Meydanı", 40.756000, 29.828000),
            new PopulerKonum("Derince Eğitim Araştırma Hst.", 40.763500, 29.825000),
            new PopulerKonum("Derince Harikalar Sahili", 40.744942, 29.798057),
            new PopulerKonum("Körfez Tütünçiftlik Sahil", 40.749500, 29.754000),
            new PopulerKonum("Körfez Yarımca Merkez", 40.772489, 29.729526),
            new PopulerKonum("Hereke Sahil & Tarihi Kale", 40.784302, 29.613195),

            // Dilovası & Gebze & Çayırova & Darıca
            new PopulerKonum("Dilovası Kent Meydanı", 40.788000, 29.542000),
            new PopulerKonum("Osmangazi Köprüsü Seyir Terası", 40.755995, 29.516096),
            new PopulerKonum("Gebze Kent Meydanı", 40.802500, 29.431000),
            new PopulerKonum("GTÜ (Gebze Teknik Üniversitesi)", 40.808000, 29.356000),
            new PopulerKonum("Darıca Hayvanat Bahçesi", 40.772500, 29.378000),
            new PopulerKonum("Darıca Millet Bahçesi", 40.748500, 29.401000),
            new PopulerKonum("Çayırova Kent Parkı", 40.830295, 29.380451),

            // Kandıra & Karadeniz Sahili
            new PopulerKonum("Kandıra Otogarı / Merkez", 41.069000, 30.153000),
            new PopulerKonum("Kerpe Sahili & Kayalıkları", 41.159506, 30.191837)
    );

    private final UlasimSistemi sistem;
    private final Collection<Durak> duraklar;

    // Başlangıç ve Varış Koordinatları (Gerçek İzmit Otogarı -> KOÜ Umuttepe Kampüsü)
    private final TextField baslangicAramaField = new TextField("İzmit Otogarı (Terminal)");
    private final TextField hedefAramaField = new TextField("KOÜ Umuttepe Kampüsü");
    private final TextField baslangicLatField = new TextField("40.775970");
    private final TextField baslangicLonField = new TextField("29.974113");
    private final TextField hedefLatField = new TextField("40.824562");
    private final TextField hedefLonField = new TextField("29.921665");

    // Parametre Kontrolleri
    private final ComboBox<String> yolcuTipiBox = new ComboBox<>();
    private final ComboBox<String> odemeYontemiBox = new ComboBox<>();

    // Harita Seçim Butonları
    private final ToggleButton secBaslangicToggle = new ToggleButton("📍 Haritadan");
    private final ToggleButton secHedefToggle = new ToggleButton("🎯 Haritadan");
    private final ToggleGroup pickGroup = new ToggleGroup();

    // Aydınlık / Koyu Tema Butonu
    private final Button temaBtn = new Button("Koyu Mod");

    // Servisler ve Bileşenler
    private final Geocoder geocoder = new Geocoder();
    private final MapView mapView;
    private final VBox sonucKutusu = new VBox(10);
    private final Label yolcuBilgiEtiketi = new Label();
    private final VBox ozetKarti = new VBox(6);
    private ScrollPane solScroll;

    public MainView(UlasimSistemi sistem) {
        this.sistem = sistem;
        this.duraklar = sistem.getGraf().getAllDuraklar();

        this.mapView = new MapView(duraklar, this::haritayaTiklandi, this::durakPopupSecildi);

        setCenter(olusturAnaDuzen());

        mapView.setOnReady(() -> {
            Platform.runLater(() -> {
                haritaMarkerlariniGuncelle();
                rotaHesaplaButonuTiklandi();
            });
        });
    }

    public void kaydir(double v) {
        if (solScroll != null) {
            Platform.runLater(() -> solScroll.setVvalue(v));
        }
    }

    private SplitPane olusturAnaDuzen() {
        VBox solPanelIcerik = olusturSolPanel();
        solScroll = new ScrollPane(solPanelIcerik);
        solScroll.setFitToWidth(true);
        solScroll.getStyleClass().add("sidebar-scroll");

        SplitPane splitPane = new SplitPane(solScroll, mapView.getNode());
        splitPane.setDividerPositions(0.38);
        SplitPane.setResizableWithParent(solScroll, false);

        return splitPane;
    }

    private VBox olusturSolPanel() {
        VBox solPanel = new VBox(10);
        solPanel.setPadding(new Insets(12, 14, 12, 14));
        solPanel.getStyleClass().add("sidebar-panel");

        // 1. Header
        HBox header = olusturHeader();

        // 2. Hızlı Rota Presetleri (Çipler)
        VBox presetKutusu = olusturPresetKutusu();

        // 3. Konum Seçim Bölümü
        VBox konumKutusu = olusturKonumKutusu();

        // 4. Parametre Ayarları Kartı
        VBox ayarKutusu = olusturAyarKutusu();

        // 5. Aksiyon Butonları
        HBox aksiyonKutusu = olusturAksiyonKutusu();

        // 6. Özet ve Sonuçlar
        ozetKarti.getStyleClass().add("summary-card");
        ozetKarti.setVisible(false);
        ozetKarti.setManaged(false);

        yolcuBilgiEtiketi.getStyleClass().add("user-info-badge");

        VBox sonuclarBolumu = new VBox(8, yolcuBilgiEtiketi, ozetKarti, sonucKutusu);

        solPanel.getChildren().addAll(
                header,
                presetKutusu,
                konumKutusu,
                ayarKutusu,
                aksiyonKutusu,
                new Separator(),
                sonuclarBolumu
        );

        return solPanel;
    }

    private HBox olusturHeader() {
        Label logoIcon = new Label("❖");
        logoIcon.setStyle("-fx-font-size: 20px; -fx-text-fill: -primary;");

        VBox baslikKutusu = new VBox(1);
        Label appTitle = new Label("TransitRoute");
        appTitle.getStyleClass().add("brand-title");

        Label appSubtitle = new Label("Kocaeli Akıllı Rota & UKOME Tarifesi");
        appSubtitle.getStyleClass().add("brand-subtitle");
        baslikKutusu.getChildren().addAll(appTitle, appSubtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Aydınlık / Koyu Tema Butonu
        temaBtn.getStyleClass().add("btn-theme-toggle");
        temaBtn.setOnAction(e -> {
            boolean koyuMu = getScene().getRoot().getStyleClass().contains("dark-theme");
            setDarkMode(!koyuMu);
        });

        Label versiyonBadge = new Label("v2.5 • Canlı");
        versiyonBadge.getStyleClass().addAll("badge-pill", "badge-eco");

        HBox header = new HBox(8, logoIcon, baslikKutusu, spacer, temaBtn, versiyonBadge);
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private VBox olusturPresetKutusu() {
        Label baslik = new Label("POPÜLER GÜZERGAHLAR");
        baslik.getStyleClass().add("section-mini-header");

        HBox satir1 = new HBox(8);
        satir1.getChildren().addAll(
                olusturPresetCip("🚌 Kampüs", 40.775970, 29.974113, "İzmit Otogarı (Terminal)", 40.824562, 29.921665, "KOÜ Umuttepe Kampüsü"),
                olusturPresetCip("🏥 Şehir Hastanesi", 40.775970, 29.974113, "İzmit Otogarı (Terminal)", 40.782254, 29.957500, "Kocaeli Şehir Hastanesi")
        );

        HBox satir2 = new HBox(8);
        satir2.getChildren().addAll(
                olusturPresetCip("🌊 Sahil Parkı", 40.775970, 29.974113, "İzmit Otogarı (Terminal)", 40.760500, 29.914000, "Sekapark Kültür Alanı"),
                olusturPresetCip("⚡ Tramvay Hattı", 40.776100, 29.974000, "Otogar Tramvay İstasyonu", 40.763500, 29.938700, "Halkevi Tramvay İstasyonu")
        );

        HBox satir3 = new HBox(8);
        satir3.getChildren().addAll(
                olusturPresetCip("🏬 Özdilek AVM", 40.763500, 29.938700, "Halkevi (İzmit Merkez)", 40.738200, 30.003500, "Özdilek AVM (Kartepe)"),
                olusturPresetCip("⚓ Gölcük Meydan", 40.763500, 29.938700, "Halkevi (İzmit Merkez)", 40.718500, 29.824000, "Gölcük Anıtpark Meydanı"),
                olusturPresetCip("🏭 Gebze Center", 40.763500, 29.938700, "Halkevi (İzmit Merkez)", 40.798500, 29.439000, "Gebze Center AVM")
        );

        VBox box = new VBox(8, baslik, satir1, satir2, satir3);
        box.getStyleClass().add("preset-card");
        return box;
    }

    private Button olusturPresetCip(String baslik, double bLat, double bLon, String bAd, double hLat, double hLon, String hAd) {
        Button btn = new Button(baslik);
        btn.getStyleClass().add("preset-chip");
        btn.setOnAction(e -> {
            baslangicLatField.setText(fmt6(bLat));
            baslangicLonField.setText(fmt6(bLon));
            baslangicAramaField.setText(bAd);

            hedefLatField.setText(fmt6(hLat));
            hedefLonField.setText(fmt6(hLon));
            hedefAramaField.setText(hAd);

            haritaMarkerlariniGuncelle();
            rotaHesaplaButonuTiklandi();
        });
        return btn;
    }

    private VBox olusturKonumKutusu() {
        secBaslangicToggle.setToggleGroup(pickGroup);
        secHedefToggle.setToggleGroup(pickGroup);
        secBaslangicToggle.getStyleClass().add("button-toggle");
        secHedefToggle.getStyleClass().add("button-toggle");

        VBox baslangicAlani = olusturNoktaAlani("📍", "Başlangıç Konumu", baslangicAramaField, baslangicLatField, baslangicLonField, secBaslangicToggle);
        VBox hedefAlani = olusturNoktaAlani("🎯", "Varış Konumu", hedefAramaField, hedefLatField, hedefLonField, secHedefToggle);

        // Değiştir (Swap) Butonu
        Button swapBtn = new Button("⇅ Noktaları Değiştir");
        swapBtn.getStyleClass().add("btn-swap");
        swapBtn.setOnAction(e -> noktalarıTakasEt());

        HBox swapKutusu = new HBox(swapBtn);
        swapKutusu.setAlignment(Pos.CENTER);

        VBox kart = new VBox(8, baslangicAlani, swapKutusu, hedefAlani);
        kart.getStyleClass().add("control-card");
        return kart;
    }

    private VBox olusturNoktaAlani(String ikon, String baslik, TextField aramaField, TextField latField, TextField lonField, ToggleButton haritaToggle) {
        Label label = new Label(ikon + " " + baslik);
        label.getStyleClass().add("field-label");

        // Popüler Kocaeli Konumları Açılır Listesi (Hızlı Seçim)
        ComboBox<String> konumSecici = new ComboBox<>();
        konumSecici.setPromptText("📍 Popüler Noktalardan Seçin (43 Konum)...");
        konumSecici.setMaxWidth(Double.MAX_VALUE);
        konumSecici.getItems().add("-- Popüler Noktalardan Seçin --");
        for (PopulerKonum p : POPULER_KONUMLAR) {
            konumSecici.getItems().add(p.ad);
        }
        konumSecici.setOnAction(e -> {
            String secim = konumSecici.getValue();
            if (secim != null && !secim.startsWith("--")) {
                POPULER_KONUMLAR.stream().filter(p -> p.ad.equals(secim)).findFirst().ifPresent(p -> {
                    aramaField.setText(p.ad);
                    latField.setText(fmt6(p.lat));
                    lonField.setText(fmt6(p.lon));
                    haritaMarkerlariniGuncelle();
                    rotaHesaplaButonuTiklandi();
                });
            }
        });

        aramaField.setPromptText(baslik + " (manuel adres yazın)...");
        HBox.setHgrow(aramaField, Priority.ALWAYS);

        Button araBtn = new Button("🔍");
        araBtn.getStyleClass().add("button-toggle");
        Runnable aramaIsi = () -> adresAra(aramaField, latField, lonField, araBtn);
        araBtn.setOnAction(e -> aramaIsi.run());
        aramaField.setOnAction(e -> aramaIsi.run());

        HBox inputSatir = new HBox(6, aramaField, araBtn);

        // Kompakt Koordinat ve Harita Seçim Satırı
        latField.setPrefWidth(85);
        lonField.setPrefWidth(85);
        latField.getStyleClass().add("text-field-mini");
        lonField.getStyleClass().add("text-field-mini");

        HBox koordinatSatir = new HBox(6, new Label("Enlem:"), latField, new Label("Boylam:"), lonField, haritaToggle);
        koordinatSatir.setAlignment(Pos.CENTER_LEFT);
        koordinatSatir.getStyleClass().add("coord-row");

        return new VBox(5, label, konumSecici, inputSatir, koordinatSatir);
    }

    private void noktalarıTakasEt() {
        String tmpAd = baslangicAramaField.getText();
        String tmpLat = baslangicLatField.getText();
        String tmpLon = baslangicLonField.getText();

        baslangicAramaField.setText(hedefAramaField.getText());
        baslangicLatField.setText(hedefLatField.getText());
        baslangicLonField.setText(hedefLonField.getText());

        hedefAramaField.setText(tmpAd);
        hedefLatField.setText(tmpLat);
        hedefLonField.setText(tmpLon);

        haritaMarkerlariniGuncelle();
        rotaHesaplaButonuTiklandi();
    }

    private VBox olusturAyarKutusu() {
        yolcuTipiBox.getItems().setAll("Genel", "Öğrenci", "Yaşlı");
        yolcuTipiBox.getSelectionModel().selectFirst();
        yolcuTipiBox.setMaxWidth(Double.MAX_VALUE);

        odemeYontemiBox.getItems().setAll("KentKart", "Kredi Kartı", "Nakit");
        odemeYontemiBox.getSelectionModel().selectFirst();
        odemeYontemiBox.setMaxWidth(Double.MAX_VALUE);

        HBox seceneklerSatir = new HBox(8,
                alanli("Yolcu Tipi", yolcuTipiBox),
                alanli("Ödeme Yöntemi", odemeYontemiBox)
        );

        // Google Haritalar Tarzı Otomatik Canlı Trafik Radarı
        TrafikDurumu anlik = TrafikDurumu.anlikTaramayiGetir();
        Label trafficRadarTitle = new Label("📡 CANLI TRAFİK RADARI (OTOMATİK)");
        trafficRadarTitle.getStyleClass().add("section-mini-header");

        Label trafficStatus = new Label(anlik.getIkon() + " " + anlik.getBaslik() + " — " + anlik.getAciklama());
        trafficStatus.getStyleClass().add("traffic-live-badge");
        trafficStatus.setWrapText(true);

        VBox kart = new VBox(8, seceneklerSatir, trafficRadarTitle, trafficStatus);
        kart.getStyleClass().add("control-card");
        return kart;
    }

    private HBox olusturAksiyonKutusu() {
        Button hesaplaBtn = new Button("⚡ Rota & Ücret Hesapla");
        hesaplaBtn.getStyleClass().add("button-primary");
        hesaplaBtn.setOnAction(e -> rotaHesaplaButonuTiklandi());
        HBox.setHgrow(hesaplaBtn, Priority.ALWAYS);
        hesaplaBtn.setMaxWidth(Double.MAX_VALUE);

        return new HBox(hesaplaBtn);
    }


    private void durakPopupSecildi(boolean isStart, double lat, double lon, String stopName) {
        Platform.runLater(() -> {
            if (isStart) {
                baslangicLatField.setText(fmt6(lat));
                baslangicLonField.setText(fmt6(lon));
                baslangicAramaField.setText(stopName);
            } else {
                hedefLatField.setText(fmt6(lat));
                hedefLonField.setText(fmt6(lon));
                hedefAramaField.setText(stopName);
            }
            haritaMarkerlariniGuncelle();
            rotaHesaplaButonuTiklandi();
        });
    }

    private void haritayaTiklandi(double lat, double lon) {
        if (secBaslangicToggle.isSelected()) {
            baslangicLatField.setText(fmt6(lat));
            baslangicLonField.setText(fmt6(lon));
            baslangicAramaField.setText("Haritadan Seçilen Nokta");
        } else if (secHedefToggle.isSelected()) {
            hedefLatField.setText(fmt6(lat));
            hedefLonField.setText(fmt6(lon));
            hedefAramaField.setText("Haritadan Seçilen Nokta");
        } else {
            return;
        }

        pickGroup.selectToggle(null);
        haritaMarkerlariniGuncelle();
        rotaHesaplaButonuTiklandi();
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
            TrafikDurumu trafik = TrafikDurumu.anlikTaramayiGetir();

            String baslangicAdi = baslangicAramaField.getText().trim();
            String hedefAdi = hedefAramaField.getText().trim();
            if (baslangicAdi.isEmpty()) baslangicAdi = "Başlangıç Noktası";
            if (hedefAdi.isEmpty()) hedefAdi = "Varış Noktası";

            List<Rota> rotalar = sistem.rotaHesapla(baslangic, hedef, baslangicAdi, hedefAdi, yolcu, trafik);


            mapView.setMarkers(baslangicLat, baslangicLon, hedefLat, hedefLon);
            mapView.clearRoute();
            sonucGoster(rotalar, yolcu, trafik);

            if (!rotalar.isEmpty()) {
                String filterType = System.getProperty("selectRouteType", "toplu").toLowerCase(Locale.ROOT);
                Rota cizilecek = rotalar.stream()
                        .filter(r -> r.getRotaAdi().toLowerCase(Locale.ROOT).contains(filterType))
                        .findFirst()
                        .orElse(rotalar.get(0));
                mapView.drawRoute(cizilecek);
            }


        } catch (NumberFormatException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Lütfen geçerli sayısal koordinat değerleri girin!");
            alert.setHeaderText(null);
            alert.showAndWait();
        }
    }

    private void sonucGoster(List<Rota> rotalar, Yolcu yolcu, TrafikDurumu trafik) {
        yolcuBilgiEtiketi.setText(String.format("👤 %s (%s)  ·  💳 %s  ·  %s",
                yolcu.isim, yolcu.tip, yolcu.odeme.getOdemeTipi(), trafik.getBaslik()));

        sonucKutusu.getChildren().clear();
        if (rotalar.isEmpty()) {
            ozetKarti.setVisible(false);
            ozetKarti.setManaged(false);
            Label ipucu = new Label("Belirtilen noktalar arasında uygun bir güzergah bulunamadı.");
            ipucu.getStyleClass().add("empty-hint");
            sonucKutusu.getChildren().add(ipucu);
            return;
        }

        // Özet Kartı Hazırla
        double maxTasarruf = rotalar.stream().mapToDouble(Rota::getTasarrufMiktari).max().orElse(0.0);
        double minCo2 = rotalar.stream().filter(r -> !r.getRotaAdi().contains("Taksi"))
                .mapToDouble(Rota::getCo2SalinimiGram).min().orElse(0.0);

        ozetKarti.getChildren().clear();
        Label ozetBaslik = new Label("📊 Güzergah & Çevre Analizi");
        ozetBaslik.getStyleClass().add("summary-title");

        Label ozetMetin = new Label(String.format("Toplu taşımayı tercih ederek direkt taksiye kıyasla %.1f TL tasarruf edebilir ve karbon salımınızı %.0f grama kadar düşürebilirsiniz.",
                maxTasarruf, minCo2));
        ozetMetin.getStyleClass().add("summary-body");
        ozetMetin.setWrapText(true);

        ozetKarti.getChildren().addAll(ozetBaslik, ozetMetin);
        ozetKarti.setVisible(true);
        ozetKarti.setManaged(true);

        for (Rota rota : rotalar) {
            sonucKutusu.getChildren().add(RouteCardFactory.olustur(rota, yolcu, () -> mapView.drawRoute(rota)));
        }
    }

    private void adresAra(TextField aramaField, TextField latField, TextField lonField, Button araButon) {
        String sorgu = aramaField.getText().trim();
        if (sorgu.isEmpty()) return;

        // 1. Önce yerel durak adlarında anında ara (Hızlı Autocomplete)
        for (Durak d : duraklar) {
            if (d.isim.toLowerCase(Locale.ROOT).contains(sorgu.toLowerCase(Locale.ROOT))) {
                latField.setText(fmt6(d.getKonum().getEnlem()));
                lonField.setText(fmt6(d.getKonum().getBoylam()));
                aramaField.setText(d.isim);
                haritaMarkerlariniGuncelle();
                return;
            }
        }

        // 2. Yerelde yoksa Nominatim Geocoder'a sor
        aramaField.getStyleClass().remove("field-error");
        araButon.setDisable(true);
        araButon.setText("⏳");

        Task<List<Geocoder.GeocodeSonuc>> gorev = new Task<>() {
            @Override
            protected List<Geocoder.GeocodeSonuc> call() throws Exception {
                return geocoder.ara(sorgu);
            }
        };

        gorev.setOnSucceeded(e -> {
            araButon.setDisable(false);
            araButon.setText("🔍");
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
            araButon.setText("🔍");
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
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
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
            case "KentKart" -> new KentKart("4141-8899", 120.0);
            case "Kredi Kartı" -> new KrediKarti("5400-****-****-1024", 1500.0);
            default -> new Nakit(80.0);
        };
    }

    private Double parseOrNull(TextField field) {
        try {
            return Double.parseDouble(field.getText().trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public void senaryoSec(int no) {
        switch (no) {
            case 1 -> {
                baslangicLatField.setText(fmt6(40.775970));
                baslangicLonField.setText(fmt6(29.974113));
                baslangicAramaField.setText("İzmit Otogarı (Terminal)");
                hedefLatField.setText(fmt6(40.824562));
                hedefLonField.setText(fmt6(29.921665));
                hedefAramaField.setText("KOÜ Umuttepe Kampüsü");
            }
            case 2 -> {
                baslangicLatField.setText(fmt6(40.775970));
                baslangicLonField.setText(fmt6(29.974113));
                baslangicAramaField.setText("İzmit Otogarı (Terminal)");
                hedefLatField.setText(fmt6(40.782254));
                hedefLonField.setText(fmt6(29.957500));
                hedefAramaField.setText("Kocaeli Şehir Hastanesi");
            }
            case 3 -> {
                baslangicLatField.setText(fmt6(40.775970));
                baslangicLonField.setText(fmt6(29.974113));
                baslangicAramaField.setText("İzmit Otogarı (Terminal)");
                hedefLatField.setText(fmt6(40.760500));
                hedefLonField.setText(fmt6(29.914000));
                hedefAramaField.setText("Sekapark Kültür Alanı");
            }
            case 4 -> {
                baslangicLatField.setText(fmt6(40.776100));
                baslangicLonField.setText(fmt6(29.974000));
                baslangicAramaField.setText("Otogar Tramvay İstasyonu");
                hedefLatField.setText(fmt6(40.761963));
                hedefLonField.setText(fmt6(29.925769));
                hedefAramaField.setText("Halkevi Tramvay İstasyonu");
            }
            case 5 -> {
                baslangicLatField.setText(fmt6(40.761963));
                baslangicLonField.setText(fmt6(29.925769));
                baslangicAramaField.setText("Halkevi (İzmit Merkez)");
                hedefLatField.setText(fmt6(40.756627));
                hedefLonField.setText(fmt6(29.948176));
                hedefAramaField.setText("41 Burda AVM");
            }
            case 6 -> {
                baslangicLatField.setText(fmt6(40.761963));
                baslangicLonField.setText(fmt6(29.925769));
                baslangicAramaField.setText("Halkevi (İzmit Merkez)");
                hedefLatField.setText(fmt6(40.744871));
                hedefLonField.setText(fmt6(30.062017));
                hedefAramaField.setText("Özdilek AVM (Kartepe)");
            }
            case 7 -> {
                baslangicLatField.setText(fmt6(40.761963));
                baslangicLonField.setText(fmt6(29.925769));
                baslangicAramaField.setText("Halkevi (İzmit Merkez)");
                hedefLatField.setText(fmt6(40.717500));
                hedefLonField.setText(fmt6(29.822500));
                hedefAramaField.setText("Gölcük Anıtpark Meydanı");
            }
            case 8 -> {
                baslangicLatField.setText(fmt6(40.761963));
                baslangicLonField.setText(fmt6(29.925769));
                baslangicAramaField.setText("Halkevi (İzmit Merkez)");
                hedefLatField.setText(fmt6(40.798500));
                hedefLonField.setText(fmt6(29.439000));
                hedefAramaField.setText("Gebze Center AVM");
            }
            case 9 -> {
                baslangicLatField.setText(fmt6(40.824562));
                baslangicLonField.setText(fmt6(29.921665));
                baslangicAramaField.setText("KOÜ Umuttepe Kampüsü");
                hedefLatField.setText(fmt6(40.773813));
                hedefLonField.setText(fmt6(29.980894));
                hedefAramaField.setText("Yuvam Akarca Konutları");
            }
            case 10 -> {
                baslangicLatField.setText(fmt6(40.824562));
                baslangicLonField.setText(fmt6(29.921665));
                baslangicAramaField.setText("KOÜ Umuttepe Kampüsü");
                hedefLatField.setText(fmt6(40.756627));
                hedefLonField.setText(fmt6(29.948176));
                hedefAramaField.setText("41 Burda AVM");
            }
        }
        haritaMarkerlariniGuncelle();
        rotaHesaplaButonuTiklandi();
    }

    public void setDarkMode(boolean dark) {
        if (getScene() != null && getScene().getRoot() != null) {
            if (dark) {
                if (!getScene().getRoot().getStyleClass().contains("dark-theme")) {
                    getScene().getRoot().getStyleClass().add("dark-theme");
                }
                temaBtn.setText("Aydınlık Mod");
                mapView.setTheme("dark");
            } else {
                getScene().getRoot().getStyleClass().remove("dark-theme");
                temaBtn.setText("Koyu Mod");
                mapView.setTheme("light");
            }
        }
    }

    private static String fmt6(double v) {
        return String.format(Locale.ROOT, "%.6f", v);
    }
}
