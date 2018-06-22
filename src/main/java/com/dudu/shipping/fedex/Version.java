package com.dudu.shipping.fedex;

import org.simpleframework.xml.Element;

public class Version {

    @Element(name = Request.VERSION + ":ServiceId")
    private String serviceId;

    @Element(name = Request.VERSION + ":Major")
    private String major;

    @Element(name = Request.VERSION + ":Intermediate")
    private String intermediate;

    @Element(name = Request.VERSION + ":Minor")
    private String minor;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getIntermediate() {
        return intermediate;
    }

    public void setIntermediate(String intermediate) {
        this.intermediate = intermediate;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }
}
