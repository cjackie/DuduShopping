package com.dudu.rest;


import com.dudu.database.DBManager;
import com.dudu.rest.exceptions.UnauthorizationException;
import com.dudu.users.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/auth")
public class Authentication {
    private static final Logger logger = LogManager.getLogger(Authentication.class);

    /**
     *
     * @param request
     * @return
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/authenticateCustomer")
    public TokenResponse authenticateCustomer(@Context HttpServletResponse response, AuthenticationRequest request) throws Exception {
        return authenticate(request, UsersManager.USER_ROLE_CUSTOMER);
    }

    /**
     *
     * @param request
     * @param role
     * @return
     * @throws UnauthorizationException
     */
    private TokenResponse authenticate(AuthenticationRequest request, char role) throws UnauthorizationException {
        DataSource source = DBManager.getManager().getDataSource(DBManager.DATABASE_DUDU_SHOPPING);

        if (request.getLogin() == null || request.getPassword() == null)
            throw new UnauthorizationException();


        User user;
        try {
            UsersManager usersManager = new UsersManager(source);
            user = usersManager.login(request.getLogin(), request.getPassword(), UsersManager.USER_ROLE_CUSTOMER);
            if (user == null)
                throw new IllegalArgumentException();

        } catch (Exception e) {
            logger.warn("Failed to logging user: " + request.getLogin(), e);
            throw new UnauthorizationException();
        }

        // obtain token
        TokenManager tokenManager = SQLTokenManager.getManager();
        try {
            Token token = tokenManager.createToken(user.getUserId());

            TokenResponse tokenResponse = new TokenResponse();
            tokenResponse.setAccessToken(token.getToken());
            tokenResponse.setTokenType("Bearer");
            tokenResponse.setExpiresIn(token.getExpiresIn());
            tokenResponse.setRefreshToken(token.getRefreshToken());

            return tokenResponse;
        } catch (Exception e) {
            logger.error("Failed to create a token for " + request.getLogin());
            throw new UnauthorizationException();
        }
    }

    /**
     *
     * @param request
     * @return
     * @throws Exception
     */
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/authenticateCustomer")
    @Secure
    public TokenResponse refreshToken(RefreshTokenRequest request) throws Exception {
        if (request.getRefreshToken() == null) {
            logger.warn("Refresh Token missing");
            throw new BadRequestException("Failed to refresh token");
        }

        try {
            TokenManager tokenManager = SQLTokenManager.getManager();
            Token token = tokenManager.refreshToken(request.getRefreshToken());

            TokenResponse tokenResponse = new TokenResponse();
            tokenResponse.setAccessToken(token.getToken());
            tokenResponse.setTokenType("Bearer");
            tokenResponse.setExpiresIn(token.getExpiresIn());
            tokenResponse.setRefreshToken(token.getRefreshToken());

            return tokenResponse;
        } catch (Exception e) {
            logger.warn(e);
            throw new BadRequestException();
        }
    }

    /*************** classes ********************/
    public static class AuthenticationRequest {
        private String login;
        private String password;

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class RefreshTokenRequest {
        private String refreshToken;

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

    public static class TokenResponse {
        private String accessToken;
        private String tokenType;
        private int expiresIn;
        private String refreshToken;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        public int getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(int expiresIn) {
            this.expiresIn = expiresIn;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }

}
