package com.dudu.shipping;

import com.dudu.net.HttpRequest;
import com.dudu.net.HttpResponse;
import com.dudu.shipping.fedex.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class FedexProxy implements ShippingTracker {
    private static Logger logger = LogManager.getLogger(FedexProxy.class);
    private final static FedexProxy instance = new FedexProxy();
    private static String url;
    private static String key;
    private static String password;
    private static String accountNumber;
    private static String meterNumber;

    public static FedexProxy getInstance() {
        return instance;
    }

    public static void configure(String url, String key, String password, String accountNumber, String meterNumber) {
        FedexProxy.url = url;
        FedexProxy.key = key;
        FedexProxy.password = password;
        FedexProxy.accountNumber = accountNumber;
        FedexProxy.meterNumber = meterNumber;
    }

    private FedexProxy() {}

    @Override
    public LinkedHashMap<Date, ShippingEvent> track(String id) {
        HttpRequest httpRequest = new HttpRequest(url);

        httpRequest.method(HttpRequest.POST)
                .addHeader("Accept", "text/html, */*")
                .addHeader("Content-Type", "text/xml");

        Request content = TrackRequest.buildRequest(id, key, password, accountNumber, meterNumber);

        SortedMap<Date, ShippingEvent> shippingEvents = new TreeMap<>();
        try {
            Serializer serializer = new Persister();

            // making request
            PrintWriter debug = new PrintWriter(System.out);
            StringWriter writer = new StringWriter();
            serializer.write(content, writer);
            serializer.write(content, debug);
            httpRequest.body(writer.getBuffer().toString());

            HttpResponse response = httpRequest.doRequest();

            if (response.status() != 200)
                throw new IllegalStateException("Expecting status = 200");

            // response
            String xml = response.responseText();
            TrackReply reply = serializer.read(TrackReply.class, xml, false);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            dateFormat.setLenient(true);
            if (reply.getCompletedTrackDetails() != null && reply.getCompletedTrackDetails().getTrackDetails() != null) {
                List<TrackDetails> trackDetailsList = reply.getCompletedTrackDetails().getTrackDetails();
                for (TrackDetails trackDetails : trackDetailsList) {
                    List<TrackDetailsEvent> events = trackDetails.getEvents();
                    if (events == null)
                        continue;

                    for (TrackDetailsEvent event : events) {
                        Date eventTime = null;
                        try {
                            eventTime = dateFormat.parse(event.getTimestamp());
                        } catch (Exception ignored) {}

                        String description = event.getEventDescription();

                        if (eventTime == null || description == null)
                            continue;

                        ShippingEvent shippingEvent = new ShippingEvent();
                        shippingEvent.setEventTime(eventTime);
                        shippingEvent.setDescription(description);
                        shippingEvents.put(eventTime, shippingEvent);
                    }
                }
            }
        } catch (Exception e) {
            logger.warn(e);
            return null;
        }

        return new LinkedHashMap<>(shippingEvents);
    }

    @Override
    public boolean identify(String id) {
        return false;
    }
}
