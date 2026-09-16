package com.ulasim.hesaplama.payment;

public class KrediKarti implements OdemeStratejisi {
    private double limit;
    private final String kartNumarasi;

    public KrediKarti(String kartNumarasi, double limit) {
        this.kartNumarasi = kartNumarasi;
        this.limit = limit;
    }

    public String getKartNumarasi() {
        return kartNumarasi;
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
