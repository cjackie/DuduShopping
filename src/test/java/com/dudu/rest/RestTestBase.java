package com.dudu.rest;

import com.dudu.common.BootstrappedTestBase;
import com.dudu.net.HttpRequest;
import com.dudu.net.HttpResponse;
import org.json.JSONObject;
import org.junit.Before;

import javax.ws.rs.core.UriBuilder;

/**
 * Created by chaojiewang on 4/27/18.
 */
public class RestTestBase extends BootstrappedTestBase {
    private String auth;
    private String host;
    private int port;
    private String scheme;
    private String contextPath;

    @Before
    public void setup() {
        super.setup();
        if (ready) {
            String host = System.getenv("HOST");
            String port = System.getenv("PORT");
            String scheme = System.getenv("SCHEME");
            String contextPath = System.getenv("CONTEXT_PATH");

            if (host == null)
                host = "localhost";
            if (port == null)
                port = "8081";
            if (scheme == null)
                scheme = "http";
            if (contextPath == null)
                contextPath = "dudu_shopping/rest";

            this.host = host;
            this.port = Integer.parseInt(port);
            this.scheme = scheme;
            this.contextPath = contextPath;
        }

    }

    protected void setAuthorization(String auth) {
        this.auth = auth;
    }

    protected UriBuilder getUriBuilder() {
        UriBuilder builder = UriBuilder.fromPath(contextPath);
        builder.scheme(scheme).host(host).port(port);
        return builder;
    }

    /**
     *
     * @param path relative path
     * @return
     */
    protected String url(String path) {
        return getUriBuilder().path(path).toString();
    }

    protected String simplePost(String url, JSONObject requestBody) throws Exception {
        HttpRequest request = new HttpRequest(url);



        request.method(HttpRequest.POST)
                .addHeader("Content-Type", "application/json")
                .body(requestBody.toString());

        if (auth != null)
            request.addHeader("Authorization", auth);

        HttpResponse response = request.doRequest();
        return response.responseText();
    }
}
