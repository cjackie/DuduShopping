package com.dudu.users;

/**
 * Created by chaojiewang on 4/29/18.
 */
public interface TokenManager {
    /**
     * check if the token is valid. if so, return the UserId
     * @param token
     * @return clientId
     * @throws Exception token is invalid
     */
    long checkToken(String token) throws Exception;

    Token createToken(long userId) throws Exception;
    Token refreshToken(String refreshToken) throws Exception;
}
