package com.dudu.shipping.fedex;

import org.simpleframework.xml.Element;

public class Address {
    @Element(name = "City")
    private java.lang.String city;

    @Element(name = "StateOrProvinceCode")
    private java.lang.String stateOrProvinceCode;

    @Element(name = "PostalCode")
    private java.lang.String postalCode;

    @Element(name = "UrbanizationCode")
    private java.lang.String urbanizationCode;

    @Element(name = "CountryCode")
    private java.lang.String countryCode;

    @Element(name = "CountryName")
    private java.lang.String countryName;

    @Element(name = "Residential")
    private java.lang.Boolean residential;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStateOrProvinceCode() {
        return stateOrProvinceCode;
    }

    public void setStateOrProvinceCode(String stateOrProvinceCode) {
        this.stateOrProvinceCode = stateOrProvinceCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getUrbanizationCode() {
        return urbanizationCode;
    }

    public void setUrbanizationCode(String urbanizationCode) {
        this.urbanizationCode = urbanizationCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Boolean getResidential() {
        return residential;
    }

    public void setResidential(Boolean residential) {
        this.residential = residential;
    }
}
