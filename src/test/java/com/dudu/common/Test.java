package com.dudu.common;

import java.net.URI;

public class Test extends TestBase {

    @org.junit.Test
    public void uriInfo() throws Exception {
        URI uri = new URI("http://www.google.com/foo/bar?ddd=3");
        println(uri.getPath());
    }

    @org.junit.Test
    public void split() throws Exception {
        String s = "/dudu_shopping/rest/auth/refreshToken";
        for (String t : s.split("/"))
            println(t);
    }
}
