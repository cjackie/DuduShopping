package com.dudu.rest;

import com.dudu.users.User;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class DuduSecurityContext implements SecurityContext {
    private User user;

    public DuduSecurityContext(User user) {
        this.user = user;
    }

    @Override
    public Principal getUserPrincipal() {
        return user;
    }

    @Override
    public boolean isUserInRole(String role) {
        return role.length() == 1 && user.getRole() == role.charAt(0);
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public String getAuthenticationScheme() {
        return null;
    }
}
