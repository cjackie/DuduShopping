package com.dudu.rest;

import com.dudu.common.BootstrappedTestBase;
import org.junit.Before;

import javax.ws.rs.core.UriBuilder;

/**
 * Created by chaojiewang on 4/27/18.
 */
public class RestTestBase extends BootstrappedTestBase {
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
}
