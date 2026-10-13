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

| ☀️ Aydınlık Mod (Esri World Street Map) | 🌙 Koyu Mod (Carbon Midnight & Night Map) |
|:---:|:---:|
| ![Aydınlık Mod](screenshot.png) | ![Koyu Mod](docs/dark-mode.png) |

### 🖼️ Popüler Güzergah Senaryoları

| 🚌 Kampüs Hattı (Otogar ➔ Umuttepe) | 🌊 Sahil Parkı (Yahya Kaptan ➔ Sekapark) |
|:---:|:---:|
| ![Kampüs](docs/01-kampus-hatti.png) | ![Sahil](docs/02-sahil-parki.png) |

| 🏬 AVM Hattı (Halkevi ➔ 41 Burda) | ⚡ Tramvay Hattı (Otogar ➔ Halkevi) |
|:---:|:---:|
| ![AVM](docs/03-avm-hatti.png) | ![Raylı](docs/04-rayli-sistem.png) |

---

## ✨ Öne Çıkan Özellikler

* **Kocaeli Popüler 16 Lokasyon Seçici:** Manuel koordinat girme zorunluluğu olmadan İzmit Otogarı, KOÜ Umuttepe Kampüsü, Kocaeli Şehir Hastanesi, Sekapark, Yahya Kaptan, Sembol AVM, 41 Burda ve Outlet Center gibi popüler noktalara açılır menüden tek tıkla rota oluşturma.
* **Resmi Kocaeli UKOME Gerçekçi Tarifeleri:** Kocaeli Büyükşehir Belediyesi UKOME kararlarıyla tam uyumlu belediye otobüsü (42 TL), Akçaray tramvay (36 TL), taksimetre açılış (55 TL), km başı (55 TL) ve indi-bindi taban ücreti (200 TL).
* **Aydınlık & Koyu Tema (Light / Dark Mode):** Tek tuşla geçiş yapılabilen `Carbon Midnight` koyu teması ve Esri Dark Gray gece haritası desteği.
* **Hızlı ve Filigransız Esri GIS Haritası:** Engellemesiz, yüksek performanslı ve akıcı harita katmanı.
* **Gerçek Sokak Geometrisi (OSRM):** Cetvel gibi düz çizgiler yerine caddeleri, bulvarları ve kavşakları takip eden kesintisiz cadde geometrisi.
* **Otomatik Canlı Trafik Radarı:** Google Haritalar mantığında; anlık saat dilimine ve pik saat yoğunluklarına göre toplu taşıma ve taksi sürelerini dinamik analiz eden sistem.
* **Çok Modlu Dijkstra Optimizasyonu:** Tramvay, otobüs, taksi ve yürüme etaplarını en hızlı ve en ekonomik kriterlere göre çözer.
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
