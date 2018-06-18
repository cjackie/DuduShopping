package com.dudu.shipping.fedex;

import org.simpleframework.xml.Element;

public class UserCredential {
    @Element(name = "Key")
    private String key;

    @Element(name = "Password")
    private String password;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
