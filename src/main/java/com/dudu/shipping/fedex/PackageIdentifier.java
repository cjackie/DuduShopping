package com.dudu.shipping.fedex;

import org.simpleframework.xml.Element;

public class PackageIdentifier {
    public static final String TRACKING_NUMBER_OR_DOORTAG = "TRACKING_NUMBER_OR_DOORTAG";

    @Element(name = Request.VERSION + ":Type")
    private String type;

    @Element(name = Request.VERSION + ":Value")
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}