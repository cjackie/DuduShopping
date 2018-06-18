package com.dudu.shipping.fedex;

import org.simpleframework.xml.Element;

public class PackageIdentifier {
    public static final String TRACKING_NUMBER_OR_DOORTAG = "TRACKING_NUMBER_OR_DOORTAG";

    @Element(name = Request.VERSION + ":Type")
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}