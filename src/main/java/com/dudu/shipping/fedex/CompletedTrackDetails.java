package com.dudu.shipping.fedex;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.List;

public class CompletedTrackDetails {
    @Element(name = "HighestSeverity")
    private String highestSeverity;

    @ElementList(inline=true, entry = "TrackDetails")
    private List<TrackDetails> trackDetails;

    public String getHighestSeverity() {
        return highestSeverity;
    }

    public void setHighestSeverity(String highestSeverity) {
        this.highestSeverity = highestSeverity;
    }

    public List<TrackDetails> getTrackDetails() {
        return trackDetails;
    }

    public void setTrackDetails(List<TrackDetails> trackDetails) {
        this.trackDetails = trackDetails;
    }
}
