package com.dudu.shipping;

import java.util.Date;

public class ShippingEvent {
    private Date eventTime;
    private String description;

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
