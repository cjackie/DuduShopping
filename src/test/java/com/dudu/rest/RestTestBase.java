package com.dudu.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.UriBuilder;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by chaojiewang on 4/27/18.
 */
public class RestTestBase {
    private static final Logger logger = LogManager.getLogger(RestTestBase.class);
    private Client client;
    private String host;
    private int port;
    private String scheme;
    private String contextPath;
    private boolean ready;
    private Properties props;

    public RestTestBase() {
        client = ClientBuilder.newClient().register(RestJsonProvider.class);
        String conf = System.getenv("REST_TEST_CONF");
        if (conf == null)
            conf = "./conf/rest_test.conf";

        props = new Properties();
        try (InputStream in = new FileInputStream(conf)) {
            props.load(in);
            String host = props.getProperty("HOST");
            String port = props.getProperty("PORT");
            String scheme = props.getProperty("SCHEME");
            String contextPath = props.getProperty("CONTEXT_PATH");

            if (host == null)
                host = "localhost";
            if (port == null)
                port = "8080";
            if (scheme == null)
                scheme = "http";
            if (contextPath == null)
                contextPath = "rest/";

            this.host = host;
            this.port = Integer.parseInt(port);
            this.scheme = scheme;
            this.contextPath = contextPath;
            this.ready = true;
        } catch (Exception ignored) {
            ready = false;
        }
    }

    protected Client getClient() {
        return client;
    }

    protected UriBuilder getUriBuilder() {
        UriBuilder builder = UriBuilder.fromPath(contextPath);
        builder.scheme(scheme).host(host).port(port);
        return builder;
    }

    protected boolean isReady() {
        return ready;
    }

    protected Properties getProps() {
        return props;
    }

}
