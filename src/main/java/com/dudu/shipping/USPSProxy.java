package com.dudu.shipping;

import java.util.Date;
import java.util.LinkedHashMap;

public class USPSProxy implements ShippingTracker {
    private final static USPSProxy instance = new USPSProxy();
    private static String api;

    public static USPSProxy getInstance() {
        return instance;
    }

    public static void configure(String api) {
        USPSProxy.api = api;
    }

    private USPSProxy() {}

    @Override
    public LinkedHashMap<Date, ShippingEvent> track(String id) {
        return null;
    }

    @Override
    public boolean identify(String id) {
        return false;
    }
}
