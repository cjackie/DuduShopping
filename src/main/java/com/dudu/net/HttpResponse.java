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

    public boolean ok() {
        try {
            return this.status() == 200;
        } catch (Exception ignored) {
            return false;
        }
    }

    public String responseText() throws IOException {
        final int MAX_SIZE = 1024*100;
        return responseText(MAX_SIZE);
    }

    public String responseText(int maxSize) throws IOException{
        byte[] buffer = new byte[maxSize];
        try (InputStream in  = conn.getInputStream()) {
            int sizeRead = 0;
            while (sizeRead < maxSize) {
                int read = in.read(buffer, sizeRead, maxSize - sizeRead);
                if (read == -1)
                    break;

                sizeRead += read;
            }

            return new String(buffer, 0, sizeRead, StandardCharsets.UTF_8);
        }
    }
}
