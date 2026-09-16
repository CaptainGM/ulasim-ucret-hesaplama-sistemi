package com.ulasim.hesaplama.service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * OpenStreetMap'in Nominatim servisi üzerinden adres/yer adı arar.
 * Nominatim kullanım politikası tanımlayıcı bir User-Agent zorunlu kılar.
 */
public class Geocoder {
    private static final String USER_AGENT = "UlasimUcretHesaplamaSistemi/1.0 (egitim projesi)";

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .build();

    public List<GeocodeSonuc> ara(String sorgu) throws Exception {
        String url = "https://nominatim.openstreetmap.org/search?format=json&limit=5&accept-language=tr&countrycodes=tr&q="
                + URLEncoder.encode(sorgu, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", USER_AGENT)
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<GeocodeSonuc> sonuclar = new ArrayList<>();
        if (response.statusCode() != 200) {
            return sonuclar;
        }

        JSONArray arr = new JSONArray(response.body());
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            double lat = Double.parseDouble(obj.getString("lat"));
            double lon = Double.parseDouble(obj.getString("lon"));
            sonuclar.add(new GeocodeSonuc(obj.getString("display_name"), lat, lon));
        }
        return sonuclar;
    }

    public record GeocodeSonuc(String isim, double lat, double lon) {
    }
}
