package com.dudu.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

public class HttpResponse {
    private HttpURLConnection conn;

    HttpResponse(HttpURLConnection conn) {
        this.conn = conn;

    }

    public int status() throws IOException {
        return conn.getResponseCode();
    }

    public String responseText() throws IOException {
        final int MAX_SIZE = 1024*100;
        return responseText(MAX_SIZE);
    }

    public String responseText(int maxSize) throws IOException{
        byte[] buffer = new byte[maxSize];
        try (InputStream in  = conn.getInputStream()) {
            int len = in.read(buffer, 0, maxSize);
            return new String(buffer, 0, len, StandardCharsets.UTF_8);
        }
    }
}
