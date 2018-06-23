package com.dudu.users;

import com.dudu.database.ZetaMap;

import java.security.Principal;
import java.util.Date;

/**
 * Created by chaojiewang on 5/10/18.
 */
public class User implements Principal {
    private long userId;
    private String login;
    private String password;
    private char role;
    private String scope;
    private Date createdOn;
    private Date lastLogin;
    private String address;

    public static User from(ZetaMap zetaMap) {
        User user = new User();
        user.setUserId(zetaMap.getLong("UserId"));
        user.setLogin(zetaMap.getString("Login"));
        user.setPassword(zetaMap.getString("Password"));
        user.setRole(zetaMap.getChar("Role"));
        user.setScope(zetaMap.getString("Scope"));
        user.setCreatedOn(zetaMap.getDate("CreatedOn"));
        user.setLastLogin(zetaMap.getDate("LastLogin"));
        user.setAddress(zetaMap.getString("Address"));
        return user;
    }

    public long getUserId() {
        return userId;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public char getRole() {
        return role;
    }

    public String getScope() {
        return scope;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public String getAddress() {
        return address;
    }

    protected void setUserId(long userId) {
        this.userId = userId;
    }

    protected void setLogin(String login) {
        this.login = login;
    }

    protected void setPassword(String password) {
        this.password = password;
    }

    protected void setRole(char role) {
        this.role = role;
    }

    protected void setScope(String scope) {
        this.scope = scope;
    }

    protected void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    protected void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    protected void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getName() {
        return getLogin() + "." + role;
    }
}
