import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public  class Main {
    public static void main(String[] args) {
        UlasimUygulamasi.main(args);
    }

    private static List<Durak> durakVerileriniOku(String dosyaYolu) {

        System.out.println("durakVerileriniOku metodu çağırıldı.");        List<Durak> durakListesi = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(dosyaYolu));
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            reader.close();
            
            JSONObject jsonObject = new JSONObject(jsonContent.toString());
            JSONArray durakArray = jsonObject.getJSONArray("duraklar");
            
            for (int i = 0; i < durakArray.length(); i++) {
                JSONObject durakObj = durakArray.getJSONObject(i);
                
                String id = durakObj.getString("id");
                String name = durakObj.getString("name");
                String type = durakObj.getString("type");
                double lat = durakObj.getDouble("lat");
                double lon = durakObj.getDouble("lon");
                boolean sonDurak = durakObj.getBoolean("sonDurak");
                
                Durak durak = new Durak(id, name, type, lat, lon, sonDurak);
                durakListesi.add(durak);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return durakListesi;
    }
    abstract static class Yolcu {
        String isim;
        String tip;
        OdemeStratejisi odeme;
    
        public Yolcu(String isim, String tip, OdemeStratejisi odeme) {
            this.isim = isim;
            this.tip = tip;
            this.odeme = odeme;
        }
        abstract double indirimHesapla(double ucret, String aracTipi);
    }
    
    static class Ogrenci extends Yolcu {
        public Ogrenci(String isim, OdemeStratejisi odeme) {
            super(isim, "Öğrenci", odeme);
        }
    
        @Override
        double indirimHesapla(double ucret, String aracTipi) {
            if ("otobus".equals(aracTipi) || "tramvay".equals(aracTipi)) {
                return ucret * 0.5; 
            }
            return ucret; 
        }
    }
    
    static class Yasli extends Yolcu {
        private int kullanımSayısı = 0;
        private final int MAKSIMUM_UCRETSIZ_KULLANIM = 20;
    
        public Yasli(String isim, OdemeStratejisi odeme) {
            super(isim, "Yaşlı", odeme);
        }
    
        @Override
        double indirimHesapla(double ucret, String aracTipi) {
            if ("otobus".equals(aracTipi) || "tramvay".equals(aracTipi)) {
                if (kullanımSayısı < MAKSIMUM_UCRETSIZ_KULLANIM) {
                    kullanımSayısı++;
                    return 0; 
                }
                return ucret * 0.5; 
            }
            return ucret; 
        }
    }
    
    static class GenelYolcu extends Yolcu {
        public GenelYolcu(String isim, OdemeStratejisi odeme) {
            super(isim, "Genel", odeme);
        }
    
        @Override
        double indirimHesapla(double ucret, String aracTipi) {
            return ucret; 
        }
    }
    

interface OdemeStratejisi {
    boolean odemeYap(double tutar);
    double getBakiye();
    String getOdemeTipi();
}

static class Nakit implements OdemeStratejisi {
    private double miktar;

    public Nakit(double miktar) {
        this.miktar = miktar;
    }

    @Override
    public boolean odemeYap(double tutar) {
        if (miktar >= tutar) {
            miktar -= tutar;
            return true;
        }
        return false;
    }

    @Override
    public double getBakiye() {
        return miktar;
    }

    @Override
    public String getOdemeTipi() {
        return "Nakit";
    }
}

static class KrediKarti implements OdemeStratejisi {
    private double limit;
    private String kartNumarasi;

    public KrediKarti(String kartNumarasi, double limit) {
        this.kartNumarasi = kartNumarasi;

        System.out.println("Kart Numarası: " + kartNumarasi);        this.limit = limit;
    }

    @Override
    public boolean odemeYap(double tutar) {
        if (limit >= tutar) {
            limit -= tutar;
            return true;
        }
        return false;
    }

    @Override
    public double getBakiye() {
        return limit;
    }

    @Override
    public String getOdemeTipi() {
        return "Kredi Kartı";
    }
}

static class KentKart implements OdemeStratejisi {
    private double bakiye;
    private String kartID;

    public KentKart(String kartID, double bakiye) {
        this.kartID = kartID;
        this.bakiye = bakiye;
    }

    @Override
    public boolean odemeYap(double tutar) {
        if (bakiye >= tutar) {
            bakiye -= tutar;
            return true;
        }
        return false;
    }

    @Override
    public double getBakiye() {
        return bakiye;
    }

    @Override
    public String getOdemeTipi() {
        return "KentKart";
    }
}

abstract static class Arac {
    String plaka;
    String tip;

    public Arac(String plaka, String tip) {
        this.plaka = plaka;
        this.tip = tip;
    }

    abstract double ucretHesapla(double mesafe);
    abstract double sureHesapla(double mesafe);
}

static class Otobus extends Arac {
    private static final double KM_UCRETI = 3.0;
    private static final double ORTALAMA_HIZ = 30.0; 

    public Otobus(String plaka) {
        super(plaka, "otobus");
    }

    @Override
    double ucretHesapla(double mesafe) {
        return mesafe * KM_UCRETI;
    }

    @Override
    double sureHesapla(double mesafe) {
        return (mesafe / ORTALAMA_HIZ) * 60;
    }
}

static class Tramvay extends Arac {
    private static final double KM_UCRETI = 2.5;
    private static final double ORTALAMA_HIZ = 25.0; 

    public Tramvay(String plaka) {
        super(plaka, "tramvay");
    }

    @Override
    double ucretHesapla(double mesafe) {
        return mesafe * KM_UCRETI;
    }

    @Override
    double sureHesapla(double mesafe) {
        return (mesafe / ORTALAMA_HIZ) * 60;
    }
}

static class Taksi extends Arac {
    private static final double ACILIS_UCRETI = 10.0;
    private static final double KM_UCRETI = 4.0;
    private static final double ORTALAMA_HIZ = 40.0; 

    public Taksi(String plaka) {
        super(plaka, "taksi");
    }

    @Override
    double ucretHesapla(double mesafe) {
        return ACILIS_UCRETI + (mesafe * KM_UCRETI);
    }

    @Override
    double sureHesapla(double mesafe) {
        return (mesafe / ORTALAMA_HIZ) * 60;
    }
}

static class Konum {
    private double enlem;
    private double boylam;

    public Konum(double enlem, double boylam) {
        this.enlem = enlem;
        this.boylam = boylam;
    }

    public double getEnlem() {
        return enlem;
    }

    public double getBoylam() {
        return boylam;
    }

    @Override
    public String toString() {
        return "(" + enlem + ", " + boylam + ")";
    }
}

interface MesafeHesaplamaStratejisi {
    double mesafeHesapla(Konum k1, Konum k2);
    String getStratejiAdi();
}

static class HaversineMesafeHesaplayici implements MesafeHesaplamaStratejisi {
    @Override
    public double mesafeHesapla(Konum k1, Konum k2) {
        final int DUNYA_YARICAPI = 6371; 
        
        double latFark = Math.toRadians(k2.getEnlem() - k1.getEnlem());
        double lonFark = Math.toRadians(k2.getBoylam() - k1.getBoylam());
        
        double a = Math.sin(latFark / 2) * Math.sin(latFark / 2)
                + Math.cos(Math.toRadians(k1.getEnlem())) * Math.cos(Math.toRadians(k2.getEnlem()))
                * Math.sin(lonFark / 2) * Math.sin(lonFark / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return DUNYA_YARICAPI * c;
    }

    @Override
    public String getStratejiAdi() {
        return "Haversine";
    }
}

static class OklidMesafeHesaplayici implements MesafeHesaplamaStratejisi {
    private static final double DERECE_KM_CARPANI = 111.32; 
    @Override
    public double mesafeHesapla(Konum k1, Konum k2) {
        double x1 = k1.getEnlem() * DERECE_KM_CARPANI;
        double y1 = k1.getBoylam() * DERECE_KM_CARPANI * Math.cos(Math.toRadians(k1.getEnlem()));
        
        double x2 = k2.getEnlem() * DERECE_KM_CARPANI;
        double y2 = k2.getBoylam() * DERECE_KM_CARPANI * Math.cos(Math.toRadians(k2.getEnlem()));
        
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    @Override
    public String getStratejiAdi() {
        return "Öklid";
    }
}

static class KonumYoneticisi {
    private static KonumYoneticisi instance;
    private MesafeHesaplamaStratejisi mesafeHesaplayici;
    
    private KonumYoneticisi() {
        mesafeHesaplayici = new HaversineMesafeHesaplayici();
    }
    
    public static KonumYoneticisi getInstance() {
        if (instance == null) {
            instance = new KonumYoneticisi();
        }
        return instance;
    }
    
    public void setMesafeHesaplayici(MesafeHesaplamaStratejisi hesaplayici) {
        this.mesafeHesaplayici = hesaplayici;
    }
    
    public double mesafeHesapla(Konum k1, Konum k2) {
        return mesafeHesaplayici.mesafeHesapla(k1, k2);
    }
    
    public String getAktifStrateji() {
        return mesafeHesaplayici.getStratejiAdi();
    }
}

static class Durak {
    String id;
    String isim;
    String tip;
    Konum konum;
    boolean sonDurak;
    
    List<Baglanti> baglantilar = new ArrayList<>();

    public Durak(String id, String isim, String tip, double enlem, double boylam, boolean sonDurak) {
        this.id = id;
        this.isim = isim;
        this.tip = tip;
        this.konum = new Konum(enlem, boylam);
        this.sonDurak = sonDurak;
    }
    
    public void baglantiEkle(Baglanti baglanti) {
        baglantilar.add(baglanti);
    }
    
    public List<Baglanti> getBaglantilar() {
        return baglantilar;
    }
    
    public Konum getKonum() {
        return konum;
    }
    
    public double mesafeHesapla(Konum digerKonum) {
        return KonumYoneticisi.getInstance().mesafeHesapla(this.konum, digerKonum);
    }

    @Override
    public String toString() {
        return isim;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Durak durak = (Durak) obj;
        return id.equals(durak.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

static class Baglanti {
    private Durak hedefDurak;
    private String aracTipi;
    private double mesafe;
    private double sure;
    private double ucret;

    public Baglanti(Durak hedefDurak, String aracTipi, double mesafe, double sure, double ucret) {
        this.hedefDurak = hedefDurak;
        this.aracTipi = aracTipi;
        this.mesafe = mesafe;
        this.sure = sure;
        this.ucret = ucret;
    }

    public Durak getHedefDurak() {
        return hedefDurak;
    }

    public String getAracTipi() {
        return aracTipi;
    }

    public double getMesafe() {
        return mesafe;
    }

    public double getSure() {
        return sure;
    }

    public double getUcret() {
        return ucret;
    }
}

static class RotaSegmenti {
    private String tip;
    private Durak baslangicDurak;
    private Durak bitisDurak;
    private double mesafe;
    private double sure;
    private double ucret;
    private String aracTipi; 

    public RotaSegmenti(String tip, Durak baslangicDurak, Durak bitisDurak, double mesafe, double sure, double ucret, String aracTipi) {
        this.tip = tip;
        this.baslangicDurak = baslangicDurak;
        this.bitisDurak = bitisDurak;
        this.mesafe = mesafe;
        this.sure = sure;
        this.ucret = ucret;
        this.aracTipi = aracTipi != null ? aracTipi : tip; 
    }
    public String getAracTipi() {
        return aracTipi;
    }
    public String getTip() {
        return tip;
    }

    public Durak getBaslangicDurak() {
        return baslangicDurak;
    }

    public Durak getBitisDurak() {
        return bitisDurak;
    }

    public double getMesafe() {
        return mesafe;
    }

    public double getSure() {
        return sure;
    }

    public double getUcret() {
        return ucret;
    }
}

static class Rota implements Comparable<Rota> {
    private List<RotaSegmenti> segmentler;
    private double toplamUcret;
    private double toplamMesafe;
    private double toplamSure;
    private int aktarmaSayisi;
    private String rotaAdi;

    public Rota(String rotaAdi) {
        this.rotaAdi = rotaAdi;
        this.segmentler = new ArrayList<>();
        this.toplamUcret = 0;
        this.toplamMesafe = 0;
        this.toplamSure = 0;
        this.aktarmaSayisi = 0;
    }

    public void ekleSegment(RotaSegmenti segment) {
        segmentler.add(segment);
        toplamUcret += segment.getUcret();
        toplamMesafe += segment.getMesafe();
        toplamSure += segment.getSure();
        
        if (segment.getTip().equals("transfer")) {
            aktarmaSayisi++;
        }
    }

    public double getToplamUcret() {
        return toplamUcret;
    }

    public double getToplamMesafe() {
        return toplamMesafe;
    }

    public double getToplamSure() {
        return toplamSure;
    }

    public int getAktarmaSayisi() {
        return aktarmaSayisi;
    }

    public String getRotaAdi() {
        return rotaAdi;
    }

    public List<RotaSegmenti> getSegmentler() {
        return segmentler;
    }

    public double hesaplaIndirimliUcret(Yolcu yolcu, RotaSegmenti segment) {
        double ucret = segment.getUcret();
        String aracTipi = segment.getAracTipi();
        return yolcu.indirimHesapla(ucret, aracTipi); 
    }
    
    @Override
    public int compareTo(Rota digerRota) {
        int sureFarki = Double.compare(this.toplamSure, digerRota.toplamSure);
        if (sureFarki != 0) return sureFarki;
        
        int ucretFarki = Double.compare(this.toplamUcret, digerRota.toplamUcret);
        if (ucretFarki != 0) return ucretFarki;
        return Integer.compare(this.aktarmaSayisi, digerRota.aktarmaSayisi);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("------- ").append(rotaAdi).append(" -------\n");
        sb.append("Toplam Mesafe: ").append(String.format("%.2f", toplamMesafe)).append(" km\n");
        sb.append("Toplam Süre: ").append(String.format("%.0f", toplamSure)).append(" dk\n");
        sb.append("Toplam Ücret: ").append(String.format("%.2f", toplamUcret)).append(" TL\n");
        sb.append("Aktarma Sayısı: ").append(aktarmaSayisi).append("\n");
        
        sb.append("Rota Detayları:\n");
        int adimSayisi = 1;
        for (RotaSegmenti segment : segmentler) {
            sb.append(adimSayisi++).append("➡ ");
            
            if (segment.getTip().equals("yurume")) {
                sb.append("🚶 Yürüme: " + segment.getBaslangicDurak().isim + " → " + segment.getBitisDurak().isim);
                sb.append(" (").append(String.format("%.2f", segment.getMesafe())).append(" km, ");
                sb.append(String.format("%.0f", segment.getSure())).append(" dk)\n");
            } else if (segment.getTip().equals("taksi")) {
                sb.append("🚖 Taksi: " + segment.getBaslangicDurak().isim + " → " + segment.getBitisDurak().isim);
                sb.append(" (").append(String.format("%.2f", segment.getMesafe())).append(" km, ");
                sb.append(String.format("%.0f", segment.getSure())).append(" dk, ");
                sb.append(String.format("%.2f", segment.getUcret())).append(" TL)\n");
            } else if (segment.getTip().equals("otobus")) {
                sb.append("🚌 Otobüs: " + segment.getBaslangicDurak().isim + " → " + segment.getBitisDurak().isim);
                sb.append(" (").append(String.format("%.2f", segment.getMesafe())).append(" km, ");
                sb.append(String.format("%.0f", segment.getSure())).append(" dk, ");
                sb.append(String.format("%.2f", segment.getUcret())).append(" TL)\n");
            } else if (segment.getTip().equals("tramvay")) {
                sb.append("🚋 Tramvay: " + segment.getBaslangicDurak().isim + " → " + segment.getBitisDurak().isim);
                sb.append(" (").append(String.format("%.2f", segment.getMesafe())).append(" km, ");
                sb.append(String.format("%.0f", segment.getSure())).append(" dk, ");
                sb.append(String.format("%.2f", segment.getUcret())).append(" TL)\n");
            } else if (segment.getTip().equals("transfer")) {
                sb.append("🔄 Transfer: " + segment.getBaslangicDurak().isim + " → " + segment.getBitisDurak().isim);
                sb.append(" (").append(String.format("%.0f", segment.getSure())).append(" dk, ");
                sb.append(String.format("%.2f", segment.getUcret())).append(" TL)\n");
            }
        }
        
        return sb.toString();
    }
}

static class UlasimGrafi {
    private Map<String, Durak> duraklar = new HashMap<>();
    private Map<String, List<String>> rotaHatları = new HashMap<>();
    public void durakEkle(Durak durak) {
        duraklar.put(durak.id, durak);
    }  
    public void baglantiEkle(String baslangicId, String hedefId, String aracTipi, double mesafe, double sure, double ucret) {
        Durak baslangicDurak = duraklar.get(baslangicId);
        Durak hedefDurak = duraklar.get(hedefId);
        
        if (baslangicDurak != null && hedefDurak != null) {
            Baglanti baglanti = new Baglanti(hedefDurak, aracTipi, mesafe, sure, ucret);
            baslangicDurak.baglantiEkle(baglanti);
        }
    }
    
    public void transferEkle(String durakId1, String durakId2, double sure, double ucret) {
        Durak durak1 = duraklar.get(durakId1);
        Durak durak2 = duraklar.get(durakId2);
        
        if (durak1 != null && durak2 != null) {
            double mesafe = KonumYoneticisi.getInstance().mesafeHesapla(durak1.getKonum(), durak2.getKonum());
            Baglanti transfer1 = new Baglanti(durak2, "transfer", mesafe, sure, ucret);
            Baglanti transfer2 = new Baglanti(durak1, "transfer", mesafe, sure, ucret);
            
            durak1.baglantiEkle(transfer1);
            durak2.baglantiEkle(transfer2);
        }
    }

    public Durak getDurak(String id) {
        return duraklar.get(id);
    }
    
    public Collection<Durak> getAllDuraklar() {
        return duraklar.values();
    }
    
    public void rotaHattıEkle(String hatAdi, List<String> durakIdleri) {
        rotaHatları.put(hatAdi, new ArrayList<>(durakIdleri));
    }
    
    public Map<String, List<String>> getRotaHatları() {
        return rotaHatları;
    }
    
    public Durak enYakinDurakBul(Konum konum, String tip) {
        Durak enYakinDurak = null;
        double enKisaMesafe = Double.MAX_VALUE;
        
        for (Durak durak : duraklar.values()) {
            if (tip == null || durak.tip.equals(tip)) {
                double mesafe = KonumYoneticisi.getInstance().mesafeHesapla(konum, durak.getKonum());
                if (mesafe < enKisaMesafe) {
                    enKisaMesafe = mesafe;
                    enYakinDurak = durak;
                }
            }
        }
        
        return enYakinDurak;
    }
    
    public Durak enYakinDurakBul(Konum konum) {
        return enYakinDurakBul(konum, null);
    }
}
static class Dijkstra {
    private UlasimGrafi graf;
    private Map<Durak, Double> uzakliklar;
    private Map<Durak, Durak> oncekiDuraklar;
    private Map<Durak, Baglanti> oncekilereGidenBaglantilar;
    private Set<Durak> ziyaretEdilmeyenDuraklar;
    
    public Dijkstra(UlasimGrafi graf) {
        this.graf = graf;
    }
    
    public List<RotaSegmenti> enKisaYoluBul(Durak baslangic, Durak hedef, String kriterTipi) {
        uzakliklar = new HashMap<>();
        oncekiDuraklar = new HashMap<>();
        oncekilereGidenBaglantilar = new HashMap<>();
        ziyaretEdilmeyenDuraklar = new HashSet<>();
        
        for (Durak durak : graf.getAllDuraklar()) {
            uzakliklar.put(durak, Double.MAX_VALUE);
            ziyaretEdilmeyenDuraklar.add(durak);
        }
        
        uzakliklar.put(baslangic, 0.0);
        
        while (!ziyaretEdilmeyenDuraklar.isEmpty()) {
            Durak suAnkiDurak = getEnKisaUzaklikliDurak();
            
            if (suAnkiDurak == null || suAnkiDurak.equals(hedef)) {
                break;
            }
            
            ziyaretEdilmeyenDuraklar.remove(suAnkiDurak);
            
            for (Baglanti baglanti : suAnkiDurak.getBaglantilar()) {
                Durak komsuDurak = baglanti.getHedefDurak();
                
                double deger;
                if ("ucret".equals(kriterTipi)) {
                    deger = baglanti.getUcret();
                } else if ("mesafe".equals(kriterTipi)) {
                    deger = baglanti.getMesafe();
                } else {
                    deger = baglanti.getSure();
                }
                
                double yeniUzaklik = uzakliklar.get(suAnkiDurak) + deger;
                
                if (yeniUzaklik < uzakliklar.get(komsuDurak)) {
                    uzakliklar.put(komsuDurak, yeniUzaklik);
                    oncekiDuraklar.put(komsuDurak, suAnkiDurak);
                    oncekilereGidenBaglantilar.put(komsuDurak, baglanti);
                }
            }
        }
        
        return yoluOlustur(baslangic, hedef);
    }
    
    private Durak getEnKisaUzaklikliDurak() {
        Durak enYakinDurak = null;
        double enKisaUzaklik = Double.MAX_VALUE;
        
        for (Durak durak : ziyaretEdilmeyenDuraklar) {
            double durakUzakligi = uzakliklar.get(durak);
            if (durakUzakligi < enKisaUzaklik) {
                enKisaUzaklik = durakUzakligi;
                enYakinDurak = durak;
            }
        }
        
        return enYakinDurak;
    }
    
    private List<RotaSegmenti> yoluOlustur(Durak baslangic, Durak hedef) {
        List<RotaSegmenti> yol = new ArrayList<>();
        Durak suAnkiDurak = hedef;
        
        if (oncekiDuraklar.get(hedef) == null) {
            return yol;
        }
        
        while (!suAnkiDurak.equals(baslangic)) {
            Durak oncekiDurak = oncekiDuraklar.get(suAnkiDurak);
            Baglanti baglanti = oncekilereGidenBaglantilar.get(suAnkiDurak);
            
            RotaSegmenti segment = new RotaSegmenti(
                baglanti.getAracTipi(),     
                oncekiDurak,                
                suAnkiDurak,                
                baglanti.getMesafe(),     
                baglanti.getSure(),         
                baglanti.getUcret(),        
                baglanti.getAracTipi()      
            );
            
            yol.add(0, segment); 
            suAnkiDurak = oncekiDurak;
        }
        
        return yol;
    }
}

static class UlasimSistemi {
    private UlasimGrafi graf;
    private static final double YURUME_HIZI = 5.0; 
    private static final double MAKSIMUM_YURUME_MESAFESI = 3.0; 
    public void ekleHedefSegmenti(Rota rota, Durak durak1, Durak durak2) {
        
        System.out.println("Hedef segmenti eklendi: " + durak1 + " -> " + durak2);
    }
    public UlasimSistemi(String jsonFilePath) {
        this.graf = new UlasimGrafi();
        try {
            yükleVeriler(jsonFilePath);
        } catch (IOException e) {
            System.err.println("Veri dosyası yüklenirken hata oluştu: " + e.getMessage());
        }
    }

    private void yükleVeriler(String jsonFilePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(jsonFilePath));
        StringBuilder jsonContent = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonContent.append(line);
        }
        reader.close();
        
        JSONObject jsonObject = new JSONObject(jsonContent.toString());
        JSONArray durakArray = jsonObject.getJSONArray("duraklar");
        
        for (int i = 0; i < durakArray.length(); i++) {
            JSONObject durakObj = durakArray.getJSONObject(i);
            
            String id = durakObj.getString("id");
            String name = durakObj.getString("name");
            String type = durakObj.getString("type");
            double lat = durakObj.getDouble("lat");
            double lon = durakObj.getDouble("lon");
            boolean sonDurak = durakObj.getBoolean("sonDurak");
            
            Durak durak = new Durak(id, name, type, lat, lon, sonDurak);
            graf.durakEkle(durak);
        }
        
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
                
                graf.rotaHattıEkle(hatAdi, durakIdleri);
            }
        }
    }

    public List<Rota> rotaHesapla(Konum baslangic, Konum hedef, Yolcu yolcu) {
        List<Rota> rotalar = new ArrayList<>();
        
        hesaplaTaksiRotasi(baslangic, hedef, rotalar);
        
        Durak enYakinBaslangicDuragi = graf.enYakinDurakBul(baslangic);
        Durak enYakinHedefDuragi = graf.enYakinDurakBul(hedef);
       
        double baslangicMesafesi = KonumYoneticisi.getInstance().mesafeHesapla(baslangic, enYakinBaslangicDuragi.getKonum());
        
        if (baslangicMesafesi <= MAKSIMUM_YURUME_MESAFESI) {
            hesaplaTopluTasimaRotasi(baslangic, hedef, "yurume", rotalar);
        } else {
            hesaplaTopluTasimaRotasi(baslangic, hedef, "taksi", rotalar);
        }
        
        hesaplaEnKisaYolRotasi(baslangic, hedef, "sure", rotalar);
        
        hesaplaEnKisaYolRotasi(baslangic, hedef, "ucret", rotalar);
        
        Collections.sort(rotalar);
        
        for (Rota rota : rotalar) {
            double indirimliToplamUcret = 0.0;
            
            for (RotaSegmenti segment : rota.getSegmentler()) {
                
                double indirimliUcret = yolcu.indirimHesapla(segment.getUcret(), segment.getAracTipi());
                indirimliToplamUcret += indirimliUcret;
                
             
            }
            
          
        }
        
        return rotalar;
    }
    
    private void hesaplaTaksiRotasi(Konum baslangic, Konum hedef, List<Rota> rotalar) {
        Rota taksiRota = new Rota("Taksi ile Direkt Rota");
        double toplamMesafe = KonumYoneticisi.getInstance().mesafeHesapla(baslangic, hedef);
        Taksi taksi = new Taksi("TAX001");
        double taksiUcret = taksi.ucretHesapla(toplamMesafe);
        double taksiSure = taksi.sureHesapla(toplamMesafe);
        
        Durak baslangicDurak = new Durak("baslangic", "Başlangıç Noktası", "özel", baslangic.getEnlem(), baslangic.getBoylam(), false);
        Durak hedefDurak = new Durak("hedef", "Hedef Noktası", "özel", hedef.getEnlem(), hedef.getBoylam(), false);
        
        RotaSegmenti taksiSegmenti = new RotaSegmenti("taksi", baslangicDurak, hedefDurak, toplamMesafe, taksiSure, taksiUcret, null);
        taksiRota.ekleSegment(taksiSegmenti);
        
        rotalar.add(taksiRota);
    }
    private void hesaplaTopluTasimaRotasi(Konum baslangic, Konum hedef, String baslangicModu, List<Rota> rotalar) {
        Durak baslangicDurak = new Durak("baslangic", "Başlangıç Noktası", "özel", baslangic.getEnlem(), baslangic.getBoylam(), false);
        Durak hedefDurak = new Durak("hedef", "Hedef Noktası", "özel", hedef.getEnlem(), hedef.getBoylam(), false);
        
        Durak enYakinBaslangicDuragi = graf.enYakinDurakBul(baslangic);
        Durak enYakinHedefDuragi = graf.enYakinDurakBul(hedef);
          
        double baslangicMesafesi = KonumYoneticisi.getInstance().mesafeHesapla(baslangic, enYakinBaslangicDuragi.getKonum());
        double bitisMesafesi = KonumYoneticisi.getInstance().mesafeHesapla(hedef, enYakinHedefDuragi.getKonum());
    
      
        boolean baslangicYurunebilir = baslangicMesafesi <= MAKSIMUM_YURUME_MESAFESI;
        boolean bitisYurunebilir = bitisMesafesi <= MAKSIMUM_YURUME_MESAFESI;
        
        
        Rota topluTasimaRota = new Rota("Toplu Taşıma Rotası");
        
      
        ekleBaslangicSegmenti(topluTasimaRota, baslangicDurak, enYakinBaslangicDuragi, baslangicYurunebilir ? "yurume" : "taksi");
        
    
        Dijkstra dijkstra = new Dijkstra(graf);
        List<RotaSegmenti> topluTasimaYolu = dijkstra.enKisaYoluBul(enYakinBaslangicDuragi, enYakinHedefDuragi, "sure");
        
        for (RotaSegmenti segment : topluTasimaYolu) {
            topluTasimaRota.ekleSegment(segment);
        }
        
       
        ekleHedefSegmenti(topluTasimaRota, enYakinHedefDuragi, hedefDurak);
        
      
        rotalar.add(topluTasimaRota);
    }

    private void hesaplaEnKisaYolRotasi(Konum baslangic, Konum hedef, String kriterTipi, List<Rota> rotalar) {
        String rotaAdi = kriterTipi.equals("ucret") ? "En Ekonomik Rota" : "En Hızlı Rota";
        
        Durak baslangicDurak = new Durak("baslangic", "Başlangıç Noktası", "özel", baslangic.getEnlem(), baslangic.getBoylam(), false);
        Durak hedefDurak = new Durak("hedef", "Hedef Noktası", "özel", hedef.getEnlem(), hedef.getBoylam(), false);
        
        Durak enYakinBaslangicDuragi = graf.enYakinDurakBul(baslangic);
        Durak enYakinHedefDuragi = graf.enYakinDurakBul(hedef);
        
        double baslangicMesafesi = KonumYoneticisi.getInstance().mesafeHesapla(baslangic, enYakinBaslangicDuragi.getKonum());
        double bitisMesafesi = KonumYoneticisi.getInstance().mesafeHesapla(hedef, enYakinHedefDuragi.getKonum());
        
        boolean baslangicYurunebilir = baslangicMesafesi <= MAKSIMUM_YURUME_MESAFESI;
        boolean bitisYurunebilir = bitisMesafesi <= MAKSIMUM_YURUME_MESAFESI;
        
        double taksiMesafe = KonumYoneticisi.getInstance().mesafeHesapla(baslangic, hedef);
        Taksi taksi = new Taksi("TAX001");
        double taksiSure = taksi.sureHesapla(taksiMesafe);
        double taksiUcret = taksi.ucretHesapla(taksiMesafe);
        
        Dijkstra dijkstra = new Dijkstra(graf);
        List<RotaSegmenti> topluTasimaYolu = dijkstra.enKisaYoluBul(enYakinBaslangicDuragi, enYakinHedefDuragi, kriterTipi);
        
        double topluTasimaUcret = 0;
        double topluTasimaSure = 0;
        for (RotaSegmenti segment : topluTasimaYolu) {
            topluTasimaUcret += segment.getUcret();
            topluTasimaSure += segment.getSure();
        }
        
        double baslangicUcret = baslangicYurunebilir ? 0 : taksi.ucretHesapla(baslangicMesafesi);
        double bitisUcret = bitisYurunebilir ? 0 : taksi.ucretHesapla(bitisMesafesi);
        
        double baslangicSure = baslangicYurunebilir ? 
            (baslangicMesafesi / YURUME_HIZI) * 60 : taksi.sureHesapla(baslangicMesafesi);
        double bitisSure = bitisYurunebilir ?
            (bitisMesafesi / YURUME_HIZI) * 60 : taksi.sureHesapla(bitisMesafesi);
        
        topluTasimaUcret += baslangicUcret + bitisUcret;
        topluTasimaSure += baslangicSure + bitisSure;
        
        // Kriterler karşılaştırma
        if (kriterTipi.equals("ucret") && taksiUcret <= topluTasimaUcret) {
            return; 
        } else if (kriterTipi.equals("sure") && taksiSure <= topluTasimaSure) {
            return; 
        }
        

        Rota enKisaRota = new Rota(rotaAdi);
        
        String baslangicModu = baslangicYurunebilir ? "yurume" : "taksi";
        ekleBaslangicSegmenti(enKisaRota, baslangicDurak, enYakinBaslangicDuragi, baslangicModu);
        
        for (RotaSegmenti segment : topluTasimaYolu) {
            enKisaRota.ekleSegment(segment);
        }
        
        ekleHedefSegmenti(enKisaRota, enYakinHedefDuragi, hedefDurak);
        
        rotalar.add(enKisaRota);
    }
    
    private void ekleBaslangicSegmenti(Rota rota, Durak baslangic, Durak hedef, String mod) {
        double mesafe = KonumYoneticisi.getInstance().mesafeHesapla(baslangic.getKonum(), hedef.getKonum());
        double sure, ucret;
        
        if (mod.equals("yurume")) {
            sure = (mesafe / YURUME_HIZI) * 60;
            ucret = 0.0; 
            RotaSegmenti yurume = new RotaSegmenti("yurume", baslangic, hedef, mesafe, sure, ucret, "yurume");
            rota.ekleSegment(yurume);
        } else if (mod.equals("taksi")) {
            Taksi taksi = new Taksi("TAX001");
            ucret = taksi.ucretHesapla(mesafe);
            sure = taksi.sureHesapla(mesafe);
            RotaSegmenti taksi1 = new RotaSegmenti("taksi", baslangic, hedef, mesafe, sure, ucret, "taksi");
            rota.ekleSegment(taksi1);
        }
    }
    public UlasimGrafi getGraf() {
        return graf;
    }
}

static class UlasimArayuzu extends JFrame {
    private UlasimSistemi sistem;
    private JTextField baslangicLatField, baslangicLonField;
    private JTextField hedefLatField, hedefLonField;
    private JComboBox<String> yolcuTipiBox, odemeYontemiBox;
    private JTextArea sonucTextArea;
    private JButton hesaplaButton;
    private double hesaplaIndirimliUcret(Yolcu yolcu, RotaSegmenti segment) {
        return yolcu.indirimHesapla(segment.getUcret(), segment.getAracTipi());
    }
    
    public UlasimArayuzu(UlasimSistemi sistem) {
        this.sistem = sistem;

        System.out.println("Sistem değişkeni: " + sistem);        setTitle("Ulaşım Sistemi");
        setSize(800, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        
        JPanel girisPanel = new JPanel();
        girisPanel.setLayout(new GridLayout(6, 2, 10, 10));
        girisPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        girisPanel.add(new JLabel("Başlangıç Enlem:"));
        baslangicLatField = new JTextField("40.7625");
        girisPanel.add(baslangicLatField);
        
        girisPanel.add(new JLabel("Başlangıç Boylam:"));
        baslangicLonField = new JTextField("30.4035");
        girisPanel.add(baslangicLonField);
        
        girisPanel.add(new JLabel("Hedef Enlem:"));
        hedefLatField = new JTextField("40.7738");
        girisPanel.add(hedefLatField);
        
        girisPanel.add(new JLabel("Hedef Boylam:"));
        hedefLonField = new JTextField("30.4252");
        girisPanel.add(hedefLonField);
        
        girisPanel.add(new JLabel("Yolcu Tipi:"));
        yolcuTipiBox = new JComboBox<>(new String[]{"Genel", "Öğrenci", "Yaşlı"});
        girisPanel.add(yolcuTipiBox);
        
        girisPanel.add(new JLabel("Ödeme Yöntemi:"));
        odemeYontemiBox = new JComboBox<>(new String[]{"KentKart", "Kredi Kartı", "Nakit"});
        girisPanel.add(odemeYontemiBox);
        
        panel.add(girisPanel, BorderLayout.NORTH);
        
        sonucTextArea = new JTextArea();
        sonucTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(sonucTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        

        JPanel butonPanel = new JPanel();
        hesaplaButton = new JButton("Rota Hesapla");
        hesaplaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rotaHesaplaButtonAction();
            }
        });
        butonPanel.add(hesaplaButton);
        
        panel.add(butonPanel, BorderLayout.SOUTH);
        
        add(panel);
        setVisible(true);
    }
    
    private void rotaHesaplaButtonAction() {
        try {
            double baslangicLat = Double.parseDouble(baslangicLatField.getText());
            double baslangicLon = Double.parseDouble(baslangicLonField.getText());
            double hedefLat = Double.parseDouble(hedefLatField.getText());
            double hedefLon = Double.parseDouble(hedefLonField.getText());
            Konum baslangic = new Konum(baslangicLat, baslangicLon);
            Konum hedef = new Konum(hedefLat, hedefLon);
            Yolcu yolcu = yolcuOlustur();
            
            List<Rota> rotalar = sistem.rotaHesapla(baslangic, hedef, yolcu);

        System.out.println("Sistem değişkeni: " + sistem);            sonucGoster(rotalar, yolcu);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Lütfen geçerli koordinat değerleri girin!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Yolcu yolcuOlustur() {
        String yolcuTipi = (String) yolcuTipiBox.getSelectedItem();
        OdemeStratejisi odemeStratejisi = odemeStratejisiOlustur();
        
        switch (yolcuTipi) {
            case "Öğrenci":
                return new Ogrenci("Kullanıcı", odemeStratejisi);
            case "Yaşlı":
                return new Yasli("Kullanıcı", odemeStratejisi);
            default:
                return new GenelYolcu("Kullanıcı", odemeStratejisi);
        }
    }
    
    private OdemeStratejisi odemeStratejisiOlustur() {
        String odemeYontemi = (String) odemeYontemiBox.getSelectedItem();
        
        switch (odemeYontemi) {
            case "KentKart":
                return new KentKart("12345", 100.0);
            case "Kredi Kartı":
                return new KrediKarti("1234-5678-9101-1121", 1000.0);
            default:
                return new Nakit(50.0);
        }
    }
    
    private void sonucGoster(List<Rota> rotalar, Yolcu yolcu) {
        StringBuilder sb = new StringBuilder();
        sb.append("Yolcu: ").append(yolcu.isim).append(" (").append(yolcu.tip).append(")\n");
        sb.append("Ödeme Yöntemi: ").append(yolcu.odeme.getOdemeTipi());
        sb.append(" (Bakiye: ").append(String.format("%.2f", yolcu.odeme.getBakiye())).append(" TL)\n\n");
        
        sb.append("HESAPLANAN ROTALAR:\n\n");
    

        for (Rota rota : rotalar) {
            sb.append(rota.toString()).append("\n");
    
            for (RotaSegmenti segment : rota.getSegmentler()) {
                double indirimliUcret = hesaplaIndirimliUcret(yolcu, segment); 
                sb.append("Arac: ").append(segment.getAracTipi()).append(" - ");
                sb.append("İndirimli Ücret: ").append(String.format("%.2f", indirimliUcret)).append(" TL\n\n");
            }
    
            sb.append("----------------------------------\n\n");
        }
    
        sonucTextArea.setText(sb.toString());
    }
}


public static class UlasimUygulamasi {
    public static void main(String[] args) {
        try {
            
            KonumYoneticisi konumYoneticisi = KonumYoneticisi.getInstance();
            konumYoneticisi.setMesafeHesaplayici(new HaversineMesafeHesaplayici());
            
            String veriDosyaYolu = "veriseti.json";
            Path dosyaYolu = Paths.get(veriDosyaYolu);
            
            String tamYol = dosyaYolu.toAbsolutePath().toString();
            
            UlasimSistemi sistem = new UlasimSistemi(tamYol);

        System.out.println("Sistem değişkeni: " + sistem);            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new UlasimArayuzu(sistem);
                }
            });
            
        } catch (Exception e) {
            System.err.println("Uygulama başlatılırken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    } 
}} 
