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
 * Leaflet tabanlı haritayı JavaFX WebView üzerinde barındıran ve
 * Java ile JavaScript (çift yönlü) etkileşimini yöneten bileşen.
 */
public class MapView {
    private final WebView webView = new WebView();
    private final WebEngine engine = webView.getEngine();
    private final Collection<Durak> baslangicDuraklari;
    private final PauseTransition resizeDebounce = new PauseTransition(Duration.millis(150));
    private boolean hazir = false;

    public interface StopSelectListener {
        void onStopSelected(boolean isStart, double lat, double lon, String stopName);
    }

    private Runnable onReadyCallback;
    private Rota bekleyenRota;

    public void setOnReady(Runnable onReady) {
        this.onReadyCallback = onReady;
        if (hazir && onReady != null) {
            onReady.run();
        }
    }

    public MapView(Collection<Durak> duraklar, BiConsumer<Double, Double> onMapClick, StopSelectListener onStopSelect) {
        this.baslangicDuraklari = duraklar;
        engine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36 TransitRoute/2.0");
        resizeDebounce.setOnFinished(e -> calistir("invalidateSize()"));


        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                hazir = true;
                calistir("setStops(" + duraklarJson() + ")");
                calistir("triggerResizeSequence()");

                if (onReadyCallback != null) {
                    onReadyCallback.run();
                }
                if (bekleyenRota != null) {
                    drawRoute(bekleyenRota);
                    bekleyenRota = null;
                }
            } else if (newState == Worker.State.FAILED) {
                System.err.println("Harita yüklenemedi: " + engine.getLoadWorker().getException());
            }
        });


        engine.setOnStatusChanged(event -> {
            String veri = event.getData();
            if (veri == null || veri.isBlank()) return;

            // Durak popup butonundan gelen seçim: STOP_SELECT:START/DEST:lat:lon:name:timestamp
            if (veri.startsWith("STOP_SELECT:")) {
                String[] parca = veri.split(":", 6);
                if (parca.length >= 5 && onStopSelect != null) {
                    boolean isStart = "START".equalsIgnoreCase(parca[1]);
                    try {
                        double lat = Double.parseDouble(parca[2]);
                        double lon = Double.parseDouble(parca[3]);
                        String name = parca[4];
                        onStopSelect.onStopSelected(isStart, lat, lon, name);
                    } catch (NumberFormatException ignored) {}
                }
                return;
            }

            // Doğrudan haritaya tıklama: MAP_CLICK:lat:lon:timestamp veya lat,lon
            if (veri.startsWith("MAP_CLICK:")) {
                String[] parca = veri.split(":");
                if (parca.length >= 3 && onMapClick != null) {
                    try {
                        double lat = Double.parseDouble(parca[1]);
                        double lon = Double.parseDouble(parca[2]);
                        onMapClick.accept(lat, lon);
                    } catch (NumberFormatException ignored) {}
                }
                return;
            }

            // Geriye dönük uyumluluk (lat,lon)
            String[] parca = veri.split(",");
            if (parca.length >= 2 && onMapClick != null) {
                try {
                    double lat = Double.parseDouble(parca[0]);
                    double lon = Double.parseDouble(parca[1]);
                    onMapClick.accept(lat, lon);
                } catch (NumberFormatException ignored) {}
            }
        });

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
        if (!hazir) {
            bekleyenRota = rota;
            return;
        }

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


    public void invalidateSize() {
        calistir("triggerResizeSequence()");
    }

    private void calistir(String script) {
        if (!hazir) return;
        try {
            engine.executeScript(script);
        } catch (Exception e) {
            System.err.println("Harita komutu çalıştırılamadı: " + script);
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
