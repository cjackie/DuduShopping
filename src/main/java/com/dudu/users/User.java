package com.dudu.users;

import com.dudu.database.ZetaMap;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chaojiewang on 5/10/18.
 */
public class User implements Principal {
    private long userId;
    private String login;
    private String password;
    private char role;
    private String rawScopes;
    private List<String> scopes;
    private Date createdOn;
    private Date lastLogin;
    private String address;

    public static User from(ZetaMap zetaMap) {
        User user = new User();
        user.setUserId(zetaMap.getLong("UserId"));
        user.setLogin(zetaMap.getString("Login"));
        user.setPassword(zetaMap.getString("Password"));
        user.setRole(zetaMap.getChar("Role"));

        String rawScopes = zetaMap.getString("Scopes");
        user.setRawScopes(rawScopes);

        List<String> scopes = new ArrayList<>();
        for (String scope : rawScopes.split(","))
            scopes.add(scope.trim());
        user.setScopes(scopes);

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

    protected void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    protected void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    protected void setAddress(String address) {
        this.address = address;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    public String getRawScopes() {
        return rawScopes;
    }

    public void setRawScopes(String rawScopes) {
        this.rawScopes = rawScopes;
    }

    @Override
    public String getName() {
        return getLogin() + "." + role;
    }
}
