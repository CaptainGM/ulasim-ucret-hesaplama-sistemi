package com.ulasim.hesaplama.distance;

import com.ulasim.hesaplama.model.Konum;

public class HaversineMesafeHesaplayici implements MesafeHesaplamaStratejisi {
    private static final int DUNYA_YARICAPI = 6371;

    @Override
    public double mesafeHesapla(Konum k1, Konum k2) {
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
