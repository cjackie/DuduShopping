package com.dudu.authorization;

/**
 * Created by chaojiewang on 4/29/18.
 */
public interface TokenManager {
    boolean isValidToken(String clientId, String secret) throws Exception;
    Token getToken(String clientId) throws Exception;
    Token createToken(String clientId) throws Exception;
    Token refreshToken(String clientId, String refreshToken) throws Exception;
}
