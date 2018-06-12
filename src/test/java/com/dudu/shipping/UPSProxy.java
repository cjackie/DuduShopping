package com.dudu.shipping;

import java.util.Date;
import java.util.LinkedHashMap;

public class UPSProxy implements ShippingTracker {
    private final static UPSProxy instance = new UPSProxy();
    private static String api;

    public static UPSProxy getInstance() {
        return instance;
    }

    public static void configure(String api) {
        UPSProxy.api = api;
    }

    private UPSProxy() {}

    @Override
    public LinkedHashMap<Date, ShippingEvent> track(String id) {
        return null;
    }

    @Override
    public boolean identify(String id) {
        return true;
    }
}
