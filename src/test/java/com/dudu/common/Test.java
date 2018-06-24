package com.dudu.common;

import java.net.URI;

public class Test extends TestBase {

    @org.junit.Test
    public void uriInfo() throws Exception{
        URI uri = new URI("http://www.google.com/foo/bar?ddd=3");
        println(uri.getPath());
    }
}
