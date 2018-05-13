package com.dudu.users;

import com.dudu.database.ZetaMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by chaojiewang on 4/29/18.
 */
public class Token {
    private String token;
    private String refreshToken;
    private int expiresIn;
    private String userId;
    private List<String> scope;
    private long id;
    private Date issuedAt;

    public static Token from(ZetaMap zmap) {
        Token token = new Token();
        token.token = zmap.getString("Token");
        token.refreshToken = zmap.getString("refreshToken");
        token.expiresIn = zmap.getInt("ExpiresIn");
        token.userId = zmap.getString("UserId");
        String scopes = zmap.getString("Scope");
        token.scope = new ArrayList<>();
        Collections.addAll(token.scope, scopes.split(","));
        token.id = zmap.getLong("Id");
        token.issuedAt = zmap.getDate("IssuedAt");

        return token;
    }

    /////////////////////////////////
    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public List<String> getScope() {
        return scope;
    }

    public long getId() {
        return id;
    }

    public Date getIssuedAt() {
        return issuedAt;
    }

    public String getUserId() {
        return userId;
    }
}
