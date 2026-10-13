# TransitRoute — Akıllı Kentsel Rota & Ücret Optimizasyonu

![Java](https://img.shields.io/badge/Java-21%2B-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-26-FF8000?style=flat-square&logo=java&logoColor=white)
![Leaflet](https://img.shields.io/badge/Leaflet-1.9-199900?style=flat-square&logo=leaflet&logoColor=white)
![OSRM](https://img.shields.io/badge/Routing-OSRM%20Street%20Engine-0055FF?style=flat-square)
![Dijkstra](https://img.shields.io/badge/Algoritma-Dijkstra-blue?style=flat-square)
![UI](https://img.shields.io/badge/Design-Opensource%20UI%20Style-6366f1?style=flat-square)

**TransitRoute**, kentsel ulaşım ağlarında otobüs, raylı sistem (tramvay), taksi ve yürüme etaplarını **Dijkstra graf algoritması** ve **OSRM sokak geometrisi** ile harmanlayan çok modlu (*multimodal*) bir rota ve ücret optimizasyon platformudur.

Anlık trafik yoğunluğunu otomatik tarar, güncel UKOME taksi & toplu taşıma tarifeleriyle gerçekçi seyahat süresi, bütçe tasarrufu ve karbon ayak izi (CO₂) analizlerini interaktif haritada sunar.

---

## 📸 Uygulama Arayüzü

![TransitRoute Arayüzü](screenshot.png)

### 🖼️ Popüler Güzergah Senaryoları

| 🚌 Kampüs Hattı (Otogar ➔ Umuttepe) | 🌊 Sahil Parkı (Yahya Kaptan ➔ Sekapark) |
|:---:|:---:|
| ![Kampüs](docs/01-kampus-hatti.png) | ![Sahil](docs/02-sahil-parki.png) |

| 🏬 AVM Hattı (Halkevi ➔ Symbol AVM) | ⚡ Tramvay Hattı (Otogar ➔ Halkevi) |
|:---:|:---:|
| ![AVM](docs/03-avm-hatti.png) | ![Raylı](docs/04-rayli-sistem.png) |

---

## ✨ Öne Çıkan Özellikler

* **Gerçek Sokak Geometrisi (OSRM):** Cetvel gibi düz çizgiler yerine caddeleri, virajları ve kavşakları takip eden gerçek dünya rotalaması.
* **Otomatik Canlı Trafik Radarı:** Google Haritalar mantığında; yerel saat ve pik saat modellerini otomatik tarar, toplu taşıma ve taksi sürelerini dinamik günceller.
* **Gerçekçi 2026 Ulaşım Tarifesi:** Güncel taksimetre açılışı (35 TL), km ücreti (25 TL), indi-bindi taban kuralı (100 TL) ve durak bekleme/aktarma süreleri.
* **Opensource UI Tasarım Çizgisi:** Tactile 3D butonlar, yüzen hap etiketler (*floating pills*), derinlikli gölgeler ve modern kart mimarisi.
* **Çok Modlu Dijkstra Optimizasyonu:** Raylı sistem, otobüs, taksi ve yürüme etaplarını hem en hızlı hem de en ekonomik kriterlere göre çözer.
* **Kişiselleştirilmiş Tarifeler:** Öğrenci (%50 indirim), Yaşlı ve Genel yolcu modelleri; KentKart, Kredi Kartı ve Nakit ödeme stratejileri.
* **Çevre & Bütçe Analitiği:** Direkt taksiye kıyasla tasarruf edilen TL, önlenen CO₂ salımı (gram) ve harcanan kalori (kcal).

---

## ⚡ Hızlı Başlatma

### Windows (Tek Tıkla)
Kök dizindeki **`start.bat`** dosyasına çift tıklayın:
```cmd
start.bat
```
*(Önceden derlenmiş sınıflar varsa 1 saniyede doğrudan açılır)*

### Komut Satırı ile Derleme ve Çalıştırma
```bash
# Projeyi derle
mvnw.cmd package

# Başlat
java -cp "target\classes;target\dependency\*" com.ulasim.hesaplama.Launcher
```

---

## 🛠️ Teknolojiler
* **Java 21+** & **JavaFX 26** (`javafx-web`, `javafx-controls`)
* **Leaflet GIS** (HOT OpenStreetMap katmanı) + **OSRM Routing Engine**
* **Dijkstra Graf Optimizasyonu** & **GoF Tasarım Desenleri** (Strategy, Façade)
