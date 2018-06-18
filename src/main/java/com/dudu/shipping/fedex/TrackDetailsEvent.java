package com.dudu.shipping.fedex;

import org.simpleframework.xml.Element;

public class TrackDetailsEvent {
    @Element(name = "Timestamp")
    private String timestamp;

    @Element(name = "EventType")
    private String eventType;

    @Element(name = "EventDescription")
    private String eventDescription;

    @Element(name = "Address")
    private Address address;

    @Element(name = "ArrivalLocation")
    private String arrivalLocation;

    ///////////////////////////////////////////////
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getArrivalLocation() {
        return arrivalLocation;
    }

    public void setArrivalLocation(String arrivalLocation) {
        this.arrivalLocation = arrivalLocation;
    }
}
