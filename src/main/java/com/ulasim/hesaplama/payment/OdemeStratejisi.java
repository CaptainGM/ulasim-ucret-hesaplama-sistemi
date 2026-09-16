package com.ulasim.hesaplama.payment;

public interface OdemeStratejisi {
    boolean odemeYap(double tutar);
    double getBakiye();
    String getOdemeTipi();
}
