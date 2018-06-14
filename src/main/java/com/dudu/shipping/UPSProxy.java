package com.dudu.shipping;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.LinkedHashMap;

public class UPSProxy implements ShippingTracker {
    private final static UPSProxy instance = new UPSProxy();
    private static final Logger logger = LogManager.getLogger(UPSProxy.class);
    private static String api;
    private static String apiKey;
    private static String username;
    private static String password;

    public static UPSProxy getInstance() {
        return instance;
    }

    public static void configure(String api, String apiKey, String username, String password) {
        UPSProxy.api = api;
        UPSProxy.apiKey = apiKey;
        UPSProxy.username = username;
        UPSProxy.password = password;
    }

    private UPSProxy() { }

    @Override
    public LinkedHashMap<Date, ShippingEvent> track(String id) {
        if (!identify(id))
            return null;

        try {
            URL url = new URL(api);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();



        } catch (Exception e) {
            logger.warn("Unable to track " + id, e);
        }
        return null;
    }

    @Override
    public boolean identify(String id) {
        return true;
    }
}
