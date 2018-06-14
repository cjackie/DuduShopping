package com.dudu.shipping;


import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;


public class ShippingTrackingRetriever implements ShippingTracker {

    private ShippingTrackingRetriever instance = new ShippingTrackingRetriever();
    private static List<ShippingTracker> trackers = new ArrayList<ShippingTracker>() {{
        add(UPSProxy.getInstance());
        add(FedexProxy.getInstance());
        add(USPSProxy.getInstance());
    }};

    private ShippingTrackingRetriever() { }


    @Override
    public LinkedHashMap<Date, ShippingEvent> track(String id) {
        return null;
    }

    @Override
    public boolean identify(String id) {
        return false;
    }


}
