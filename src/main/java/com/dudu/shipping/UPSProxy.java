package com.dudu.shipping;

import com.dudu.net.HttpRequest;
import com.dudu.net.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    static void configure(String api, String apiKey, String username, String password) {
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

        String requestData = "       " +
                "        {\n" +
                "            \"UPSSecurity\": {\n" +
                "                \"UsernameToken\": {\n" +
                "                    \"Username\": \"%s\",\n" +
                "                    \"Password\": \"%s\"\n" +
                "                },\n" +
                "                \"ServiceAccessToken\": {\n" +
                "                    \"AccessLicenseNumber\": \"%s\"\n" +
                "                }\n" +
                "            },\n" +
                "            \"TrackRequest\": {\n" +
                "                \"Request\": {\n" +
                "                    \"RequestOption\": \"activity\",\n" +
                "                    \"TransactionReference\": {\n" +
                "                        \"CustomerContext\": \"Your Test Case Summary Description\"\n" +
                "                    }\n" +
                "                },\n" +
                "                \"InquiryNumber\": \"%s\"\n" +
                "            }\n" +
                "        }";
        requestData = String.format(requestData, username, password, apiKey, id);

        // request object
        HttpRequest httpRequest = new HttpRequest(api);
        httpRequest.method(HttpRequest.GET)
                .body(requestData);

        JSONObject trackingResponse = null;
        try {
            HttpResponse response =  httpRequest.doRequest();
            if (response.status() != 200)
                return null;

            trackingResponse = new JSONObject(new JSONTokener(response.responseText())).getJSONObject("TrackResponse");


        } catch (Exception e) {
            logger.warn("Failed to get shipping event.", e);
            return null;
        }

        // try Activity
        try {
            JSONArray activities = trackingResponse.getJSONObject("Shipment").getJSONArray("Activity");
            SortedMap<Date, ShippingEvent> activiesMap = new TreeMap<>(Date::compareTo);

            for (int i = 0; i < activities.length(); i++) {
                JSONObject activity = activities.getJSONObject(i);
                String city = activity.getJSONObject("ActivityLocation").getString("City");
                String stateProvinceCode = activity.getJSONObject("ActivityLocation").getString("StateProvinceCode");
                String description = activity.getString("Description");
                String date = activity.getString("Date");
                String time = activity.getString("Time");

                String datetime = date + " " + time;
                Date trackTime = null;
                SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd hhmmss");
                SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd hhmm");

                try {
                    trackTime = dateFormat1.parse(datetime);
                } catch (ParseException ignore) { }

                if (trackTime == null) {
                    trackTime = dateFormat2.parse(datetime);
                }

                ShippingEvent event = new ShippingEvent();
                event.setEventTime(trackTime);
                event.setDescription(String.format("At %s %s, %s", city, stateProvinceCode, description));
                activiesMap.put(trackTime, event);

            }

            return new LinkedHashMap<>(activiesMap);
        } catch (Exception ignored) { }

        /** case 2 **/

        try {
            JSONObject activity = trackingResponse.getJSONObject("Shipment").getJSONObject("Package").getJSONObject("Activity");
            String city = activity.getJSONObject("ActivityLocation").getJSONObject("Address").getString("City");
            String stateProvinceCode = activity.getJSONObject("ActivityLocation").getJSONObject("Address").getString("StateProvinceCode");

            String date = activity.getString("Date");
            String time = activity.getString("Time");
            String description = activity.getJSONObject("Status").getString("Description");

            String datetime = date + " " + time;
            Date trackTime = null;
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd hhmmss");
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd hhmm");

            try {
                trackTime = dateFormat1.parse(datetime);
            } catch (ParseException ignore) { }

            if (trackTime == null) {
                trackTime = dateFormat2.parse(datetime);
            }

            ShippingEvent event = new ShippingEvent();
            event.setEventTime(trackTime);
            event.setDescription(String.format("At %s %s, %s", city, stateProvinceCode, description));

            LinkedHashMap<Date, ShippingEvent> activities = new LinkedHashMap<>();
            activities.put(trackTime, event);

            return activities;
        } catch (Exception igored) { }

        logger.warn("Failed to track UPS: " + id);
        return null;
    }

    @Override
    public boolean identify(String id) {
        return true;
    }
}
