package com.dudu.net;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * for both https and http
 */
public class HttpRequest {
    public static final String POST = "POST";
    public static final String GET = "GET";

    private final String USER_AGENT = "Mozilla/5.0";

    private String url;
    private String method;
    private String body;
    private LinkedHashMap<String, String> queryParam;
    private LinkedHashMap<String, String> headers;
    private HttpURLConnection conn;

    public HttpRequest(String url) {
        this.url = url;
        this.method = "GET";
        this.body = "";
        this.headers = new LinkedHashMap<>();
        this.queryParam = new LinkedHashMap<>();
    }


    public HttpRequest(String schema, String host, String path) {
        this.url = schema + "://" + host + (host.charAt(host.length()-1) != '/' ? "/" : "") + path;
        this.method = GET;
        this.body = "";
        this.headers = new LinkedHashMap<>();
        this.queryParam = new LinkedHashMap<>();
    }

    public HttpRequest method(String method) {
        this.method = method;
        return this;
    }

    public HttpRequest addQueryParam(String name, String value) {
        queryParam.put(name, value);
        return this;
    }

    public HttpRequest addHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public HttpRequest body(String body) {
        this.body = body;
        return this;
    }

    public HttpResponse doRequest() throws Exception {
        String url = this.url;
        List<String> paramters = new ArrayList<>(queryParam.keySet());
        for (int i = 0; i < paramters.size(); i++) {
            String param = paramters.get(i);
            if (i == 0)
                url += "?" + param + "=" + urlEncode(queryParam.get(param));
        }

        URLConnection conn = new URL(url).openConnection();
        if (conn instanceof HttpURLConnection) {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod(method);

            for (String key : headers.keySet())
                httpConn.addRequestProperty(key, headers.get(key));

            httpConn.addRequestProperty("User-Agent", USER_AGENT);
            if (body.length() != 0 ) {
                httpConn.setRequestProperty("Content-Length", Integer.toString(body.length()));
                httpConn.setDoOutput(true);
                try (OutputStream out = httpConn.getOutputStream()) {
                    out.write(body.getBytes("UTF8"));
                    out.flush();
                }
            }

            return new HttpResponse(httpConn);
        } else
            throw new IllegalStateException("Unknown url connection");

    }

    private String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
