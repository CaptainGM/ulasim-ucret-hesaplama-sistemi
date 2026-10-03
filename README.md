# Ulaşım Ücret ve Rota Hesaplama Sistemi

İki nokta arasında otobüs, tramvay ve taksiyi birlikte değerlendirip en uygun rotayı, süreyi ve ücreti hesaplayan JavaFX masaüstü uygulaması. Başlangıç ve hedef nokta haritadan seçilebiliyor, hesaplanan rota harita üzerinde çiziliyor.

Not: `screenshot.png` eski Swing arayüzünden kalma, güncel değil.

## Neler var

- Duraklar arasında graf ve Dijkstra ile rota hesabı (`UlasimGrafi`, `Rota`, `RotaSegmenti`, `Dijkstra`)
- Başlangıç ve hedefe en yakın durağa yürüyerek ya da taksiyle gitme
- Araç tipine göre ücret ve süre:
  - Otobüs 3.0 TL/km, tramvay 2.5 TL/km, taksi 10 TL açılış + 4.0 TL/km
- Yolcu tipine göre indirim:
  - Öğrenci: otobüs ve tramvayda %50
  - Yaşlı: ilk 20 kullanım ücretsiz, sonra %50
  - Genel: indirim yok
- Ödeme yöntemleri: nakit, kredi kartı, KentKart (Strateji deseni ile)
- Harita üzerinde nokta seçme, rota araç tipine göre renkli çiziliyor
- Adres ya da yer adıyla arama (Nominatim), enlem/boylam bilmeye gerek yok

## Kullanılanlar

Java 21+, JavaFX 26, org.json, Leaflet + OpenStreetMap (`src/main/resources/map` içinde), Nominatim, Maven (Maven Wrapper dahil, ayrıca kurmak gerekmiyor)

Duraklar `src/main/resources/veriseti.json` dosyasında (id, isim, tip, enlem/boylam, son durak bilgisi).

## Çalıştırma

En kolayı `start.bat` dosyasına çift tıklamak. Maven Wrapper ile bağımlılıkları indirip (ilk seferde internet gerekir) derliyor ve uygulamayı açıyor.

Elle:

```
mvnw.cmd package
java -cp "target\classes;target\dependency\*" com.ulasim.hesaplama.Launcher
```

`mvn javafx:run` bu projede çalışmıyor: JDK 26'da `jdk.jsobject` modülü yok ve `javafx-maven-plugin` 0.0.8 onun yerine geçen `org.openjfx:jdk-jsobject` bağımlılığını module path yerine classpath'e koyuyor. Bu yüzden bağımlılıklar `target/dependency` içine kopyalanıyor ve uygulama `Launcher` sınıfı üzerinden düz classpath ile açılıyor (`Application` alt sınıfını doğrudan çalıştırınca JavaFX "runtime components missing" hatası veriyor).

Arayüzde varsayılan koordinatlarla "Rota Hesapla"ya basılabilir, "Haritadan Seç" ile haritaya tıklanabilir ya da arama kutularına adres yazıp Enter'a basılabilir. Harita görüntüsü ve adres arama için internet bağlantısı gerekiyor.
