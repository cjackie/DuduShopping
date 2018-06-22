package com.dudu.shipping;

import java.util.Date;
import java.util.LinkedHashMap;

public interface ShippingTracker {


    /**
     * sorted list by time in ascending order.
     *
     * @param id
     * @return null on error
     */
    LinkedHashMap<Date, ShippingEvent> track(String id);

    /**
     *
     * @param id
     * @return false when @id is not valid, true when it might be valid.
     */
    boolean identify(String id);
}
