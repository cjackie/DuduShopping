package com.dudu.rest;

import org.json.JSONObject;
import org.junit.Assume;
import org.junit.Test;


public class AuthenticationTest extends RestTestBase {

    public void setup() {
        super.setup();

        Assume.assumeTrue(ready);
    }

    @Test
    public void authenticateCustomer() throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("login", "jack");
        requestBody.put("password", "test123");

        println(simplePost(url("/auth/authenticateCustomer"), requestBody));
    }

    @Test
    public void refreshToken() throws Exception {
        setAuthorization("Bearer CXf+s5V4e9Xs2bVQyjy2hMhco0jhsvNQ2R9CEmLsQNhSzFZWhe63VXmK7Lvt84SOjsMelxjWm+0S0LjR");

        JSONObject requestBody = new JSONObject();
        requestBody.put("refreshToken", "aKia/TU6r+7upIV0azWAIlgrN+uAp1ybvczArZ5sIwgPw4rzIq/EHILRIo4yDfsELfN1qHgpIEOlf0cd");

        println(simplePost(url("auth/refreshToken"), requestBody));
    }
}
