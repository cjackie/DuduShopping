package com.dudu.token;

import com.dudu.database.ZetaMap;

import java.util.Date;

/**
 * Created by chaojiewang on 4/29/18.
 */
public class Token {
    private String secret;
    private String login;
    private int id;
    private int GoodFor;
    private Date createdOn;

    public static Token from(ZetaMap zmap) {
        Token token = new Token();
        token.setLogin(zmap.getString("Login"));
        token.setId(zmap.getInt("Id"));
        token.setCreatedOn(zmap.getDate("CreatedOn"));
        token.setGoodFor(zmap.getInt("GoodFor"));
        token.setSecret(zmap.getString("Secret"));
        return token;
    }

    /////////////////////////////////
    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGoodFor() {
        return GoodFor;
    }

    public void setGoodFor(int goodFor) {
        GoodFor = goodFor;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
