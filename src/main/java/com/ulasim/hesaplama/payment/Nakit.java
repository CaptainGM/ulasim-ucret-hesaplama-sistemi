package com.ulasim.hesaplama.payment;

public class Nakit implements OdemeStratejisi {
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
