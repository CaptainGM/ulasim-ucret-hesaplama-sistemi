package com.ulasim.hesaplama.distance;

import com.ulasim.hesaplama.model.Konum;

public class OklidMesafeHesaplayici implements MesafeHesaplamaStratejisi {
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
