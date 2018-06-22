package com.dudu.shipping.fedex;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.List;

public class TrackDetails {
    @Element(name = "ServiceCommitMessage")
    private String serviceCommitMessage;

    @ElementList(entry = "Events", inline = true)
    private List<TrackDetailsEvent> events;

    public String getServiceCommitMessage() {
        return serviceCommitMessage;
    }

    public void setServiceCommitMessage(String serviceCommitMessage) {
        this.serviceCommitMessage = serviceCommitMessage;
    }

    public List<TrackDetailsEvent> getEvents() {
        return events;
    }

    public void setEvents(List<TrackDetailsEvent> events) {
        this.events = events;
    }

}
