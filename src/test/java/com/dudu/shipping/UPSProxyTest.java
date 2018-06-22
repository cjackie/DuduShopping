package com.dudu.shipping;

import org.junit.Assume;
import org.junit.Test;

import java.util.Date;
import java.util.LinkedHashMap;

public class UPSProxyTest extends ShippingTestBase {

    @Test
    public void track() {
        Assume.assumeTrue(ready);
        UPSProxy proxy = UPSProxy.getInstance();

        LinkedHashMap<Date, ShippingEvent> events = proxy.track("990728071");
        println(events.size());

        events = proxy.track("1Z12345E1305277940");
        println(events.size());
    }
}
