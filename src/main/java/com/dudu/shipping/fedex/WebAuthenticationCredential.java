package com.dudu.shipping.fedex;

import org.simpleframework.xml.Element;

public class WebAuthenticationCredential {

    @Element(name = Request.VERSION + ":UserCredential")
    private UserCredential userCredential;

    public UserCredential getUserCredential() {
        return userCredential;
    }

    public void setUserCredential(UserCredential userCredential) {
        this.userCredential = userCredential;
    }
}
