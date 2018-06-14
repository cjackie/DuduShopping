package com.dudu.shipping;

import java.util.Date;
import java.util.LinkedHashMap;

public class FedexProxy implements ShippingTracker {
    private final static FedexProxy instance = new FedexProxy();
    private static String api;

    public static FedexProxy getInstance() {
        return instance;
    }

    public static void configure(String api) {
        FedexProxy.api = api;
    }

    private FedexProxy() {}

    @Override
    public LinkedHashMap<Date, ShippingEvent> track(String id) {
        return null;
    }

    @Override
    public boolean identify(String id) {
        return false;
    }
}
