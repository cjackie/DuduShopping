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
    private long userId;
    private List<String> scope;
    private long id;
    private Date issuedAt;

    public static Token from(ZetaMap zmap) {
        Token token = new Token();
        token.token = zmap.getString("Token");
        token.refreshToken = zmap.getString("RefreshToken");
        token.expiresIn = zmap.getInt("ExpiresIn");
        token.userId = zmap.getLong("UserId");
        String scopes = zmap.getString("Scope");
        token.scope = new ArrayList<>();
        Collections.addAll(token.scope, scopes.split(","));
        token.id = zmap.getLong("Id");
        token.issuedAt = zmap.getDate("IssuedAt");

        return token;
    }

    /////////////////////////////////

    /**
     * prefix.secret.suffix
     * @return
     */
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

    public long getUserId() {
        return userId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setScope(List<String> scope) {
        this.scope = scope;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setIssuedAt(Date issuedAt) {
        this.issuedAt = issuedAt;
    }
}
