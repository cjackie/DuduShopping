package com.dudu.net;

import com.dudu.common.TestBase;
import org.json.JSONObject;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

public class HttpRequestTest extends TestBase {

    @Before
    public void setup() {
        super.setup();
    }

    @Test
    public void doRequest() throws Exception {
        Assume.assumeTrue(ready);

        HttpRequest request = new HttpRequest("https://jsonplaceholder.typicode.com/posts");
        request.method(HttpRequest.METHOD_GET);
        HttpResponse response = request.doRequest();
        System.out.println(response.responseText());
    }

    @Test
    public void doRequest2() throws Exception {
        Assume.assumeTrue(ready);

        HttpRequest request = new HttpRequest("https", "jsonplaceholder.typicode.com", "posts");
        request.addHeader("Content-Type", "application/json; charset=UTF-8");

        JSONObject obj = new JSONObject();
        obj.put("title", "foo");
        obj.put("body", "bar");
        obj.put("userId", 1);

        request.body(obj.toString());

        HttpResponse response = request.doRequest();
        println(response.responseText());
    }
}
