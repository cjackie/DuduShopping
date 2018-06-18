package com.dudu.shipping.fedex;

import org.simpleframework.xml.Element;

public class ClientDetail {
    @Element(name = "AccountNumber")
    private String accountNumber;

    @Element(name = "MeterNumber")
    private String meterNumber;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getMeterNumber() {
        return meterNumber;
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }
}
