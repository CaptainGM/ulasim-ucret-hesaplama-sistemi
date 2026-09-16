package com.ulasim.hesaplama.ui.map;

import com.ulasim.hesaplama.model.Durak;
import com.ulasim.hesaplama.model.Rota;
import com.ulasim.hesaplama.model.RotaSegmenti;
import javafx.animation.PauseTransition;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Locale;
import java.util.function.BiConsumer;

/**
 * JDK 26 no longer ships the {@code jdk.jsobject} module, so {@code netscape.javascript.JSObject}
 * is unavailable here. All Java-to-JS calls go through {@link WebEngine#executeScript}, and
 * JS-to-Java clicks are relayed via the classic {@code window.status} event instead of a JSObject bridge.
 */
public class MapView {
    private final WebView webView = new WebView();
    private final WebEngine engine = webView.getEngine();
    private final Collection<Durak> baslangicDuraklari;
    private final PauseTransition resizeDebounce = new PauseTransition(Duration.millis(200));
    private boolean hazir = false;

    public MapView(Collection<Durak> duraklar, BiConsumer<Double, Double> onMapClick) {
        this.baslangicDuraklari = duraklar;
        resizeDebounce.setOnFinished(e -> calistir("invalidateSize()"));

        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                hazir = true;
                calistir("setStops(" + duraklarJson() + ")");
                calistir("invalidateSize()");
            } else if (newState == Worker.State.FAILED) {
                System.err.println("Harita yüklenemedi: " + engine.getLoadWorker().getException());
            }
        });

        engine.setOnStatusChanged(event -> {
            String veri = event.getData();
            if (veri == null || veri.isBlank()) return;
            String[] parca = veri.split(",");
            if (parca.length < 2) return;
            try {
                double lat = Double.parseDouble(parca[0]);
                double lon = Double.parseDouble(parca[1]);
                onMapClick.accept(lat, lon);
            } catch (NumberFormatException ignored) {
                // haritadan gelen başka bir status mesajı, yok say
            }
        });

        // Layout değişince (bölme çubuğu, pencere boyutu) haritayı yeniden ölçeklendir;
        // ard arda gelen olaylar tek bir gecikmeli invalidateSize() çağrısına indirgenir.
        webView.widthProperty().addListener((obs, o, n) -> {
            resizeDebounce.stop();
            resizeDebounce.playFromStart();
        });
        webView.heightProperty().addListener((obs, o, n) -> {
            resizeDebounce.stop();
            resizeDebounce.playFromStart();
        });

        engine.load(getClass().getResource("/map/map.html").toExternalForm());
    }

    public WebView getNode() {
        return webView;
    }

    public void setMarkers(double lat1, double lon1, double lat2, double lon2) {
        calistir(String.format(Locale.ROOT, "setMarkers(%s,%s,%s,%s)",
                sayi(lat1), sayi(lon1), sayi(lat2), sayi(lon2)));
    }

    public void clearRoute() {
        calistir("clearRoute()");
    }

    public void drawRoute(Rota rota) {
        JSONArray segmentler = new JSONArray();
        for (RotaSegmenti segment : rota.getSegmentler()) {
            JSONObject obj = new JSONObject();
            obj.put("tip", segment.getAracTipi());

            JSONArray coords = new JSONArray();
            coords.put(noktaJson(segment.getBaslangicDurak()));
            coords.put(noktaJson(segment.getBitisDurak()));
            obj.put("coords", coords);

            segmentler.put(obj);
        }

        calistir("drawRoute(" + segmentler + ")");
    }

    private void calistir(String script) {
        if (!hazir) return;
        try {
            engine.executeScript(script);
        } catch (Exception e) {
            System.err.println("Harita komutu çalıştırılamadı: " + script);
            e.printStackTrace();
        }
    }

    private JSONArray noktaJson(Durak durak) {
        return new JSONArray()
                .put(durak.getKonum().getEnlem())
                .put(durak.getKonum().getBoylam());
    }

    private String duraklarJson() {
        JSONArray arr = new JSONArray();
        for (Durak d : baslangicDuraklari) {
            JSONObject obj = new JSONObject();
            obj.put("name", d.isim);
            obj.put("tip", d.tip);
            obj.put("lat", d.getKonum().getEnlem());
            obj.put("lon", d.getKonum().getBoylam());
            arr.put(obj);
        }
        return arr.toString();
    }

    private static String sayi(double v) {
        return Double.toString(v);
    }
}
