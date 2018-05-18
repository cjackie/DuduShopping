package com.dudu.users;

import java.util.List;

/**
 * Created by chaojiewang on 4/29/18.
 */
public interface TokenManager {
    boolean isValidToken(String clientId, String secret) throws Exception;
    List<Token> getTokens(String clientId) throws Exception;
    Token createToken(String clientId) throws Exception;
    Token refreshToken(String clientId, String refreshToken) throws Exception;
}
