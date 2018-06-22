package com.dudu.shipping;

import org.junit.Test;

import java.util.Date;
import java.util.LinkedHashMap;

public class FedexProxyTest extends ShippingTestBase {

    @Test
    public void track() throws Exception {
        String trackId = "920241085725456";

        LinkedHashMap<Date, ShippingEvent> events = FedexProxy.getInstance().track(trackId);
        println(events.size());
    }
}
