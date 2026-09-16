package com.ulasim.hesaplama.distance;

import com.ulasim.hesaplama.model.Konum;

public interface MesafeHesaplamaStratejisi {
    double mesafeHesapla(Konum k1, Konum k2);
    String getStratejiAdi();
}
