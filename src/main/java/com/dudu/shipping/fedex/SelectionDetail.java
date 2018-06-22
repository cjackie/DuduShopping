package com.dudu.shipping.fedex;

import org.simpleframework.xml.Element;

public class SelectionDetail {
    public static final String PROCESSING_OPTIONS_INCLUDE_DETAILED_SCANS = "INCLUDE_DETAILED_SCANS";

    @Element(name = Request.VERSION + ":PackageIdentifier")
    private PackageIdentifier packageIdentifier;

    /**
     * Valid value is INCLUDE_DETAILED_SCANS.
     * If FALSE (the default), the reply will contain summary/profile data including current status.
     * If TRUE, the reply will contain profile and detailed scan activity (multiple TrackDetail objects) for each package.
     */
    @Element(name = Request.VERSION + ":ProcessingOptions")
    private String processingOptions;

    public String getProcessingOptions() {
        return processingOptions;
    }

    public void setProcessingOptions(String processingOptions) {
        this.processingOptions = processingOptions;
    }

    public PackageIdentifier getPackageIdentifier() {
        return packageIdentifier;
    }

    public void setPackageIdentifier(PackageIdentifier packageIdentifier) {
        this.packageIdentifier = packageIdentifier;
    }
}
