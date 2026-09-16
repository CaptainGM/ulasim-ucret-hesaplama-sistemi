package com.ulasim.hesaplama.payment;

public class KentKart implements OdemeStratejisi {
    private double bakiye;
    private final String kartID;

    public KentKart(String kartID, double bakiye) {
        this.kartID = kartID;
        this.bakiye = bakiye;
    }

    public String getKartID() {
        return kartID;
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
