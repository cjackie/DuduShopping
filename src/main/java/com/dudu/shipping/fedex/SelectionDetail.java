package com.dudu.shipping.fedex;

import org.simpleframework.xml.Element;

public class SelectionDetail {
    public static final String PROCESSING_OPTIONS_INCLUDE_DETAILED_SCANS = "INCLUDE_DETAILED_SCANS";

    @Element(name = "TrackingNumberUniqueIdentifier")
    private String trackingNumberUniqueIdentifier;

    /**
     * Valid value is INCLUDE_DETAILED_SCANS.
     * If FALSE (the default), the reply will contain summary/profile data including current status.
     * If TRUE, the reply will contain profile and detailed scan activity (multiple TrackDetail objects) for each package.
     */
    @Element(name = "ProcessingOptions")
    private String processingOptions;

    public String getTrackingNumberUniqueIdentifier() {
        return trackingNumberUniqueIdentifier;
    }

    public void setTrackingNumberUniqueIdentifier(String trackingNumberUniqueIdentifier) {
        this.trackingNumberUniqueIdentifier = trackingNumberUniqueIdentifier;
    }

    public String getProcessingOptions() {
        return processingOptions;
    }

    public void setProcessingOptions(String processingOptions) {
        this.processingOptions = processingOptions;
    }
}
