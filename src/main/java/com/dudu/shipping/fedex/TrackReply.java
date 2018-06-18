package com.dudu.shipping.fedex;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(strict = false)
public class TrackReply {
    @Element(name = "HighestSeverity")
    private String highestSeverity;

    @Element(name = "CompletedTrackDetails")
    private CompletedTrackDetails completedTrackDetails;

    public String getHighestSeverity() {
        return highestSeverity;
    }

    public void setHighestSeverity(String highestSeverity) {
        this.highestSeverity = highestSeverity;
    }

    public CompletedTrackDetails getCompletedTrackDetails() {
        return completedTrackDetails;
    }

    public void setCompletedTrackDetails(CompletedTrackDetails completedTrackDetails) {
        this.completedTrackDetails = completedTrackDetails;
    }
}
