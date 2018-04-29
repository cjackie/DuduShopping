package com.dudu.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * Created by chaojiewang on 4/28/18.
 */
public class CryptoUtil {

    /**
     * apply sha256, then base 64
     * @param in
     * @return
     */
    public static String sha256base64(String in) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha256.digest(in.getBytes(StandardCharsets.US_ASCII));
            return base64(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String base64(byte[] bytes) {
        byte[] encoded = Base64.getEncoder().encode(bytes);
        char[] result = new char[encoded.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = (char) encoded[i];
        }
        return String.valueOf(result);
    }
}
