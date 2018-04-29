package com.dudu.token;

/**
 * Created by chaojiewang on 4/29/18.
 */
public interface TokenManager {
    boolean isValidToken(String clientId, String secret);
    Token getToken(String clientId);
    Token createToken(String clientId);
}
