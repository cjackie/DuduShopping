package com.dudu.rest;

import com.dudu.database.DBManager;
import com.dudu.rest.exceptions.UnauthorizationException;
import com.dudu.users.SQLTokenManager;
import com.dudu.users.TokenManager;
import com.dudu.users.User;
import com.dudu.users.UsersManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Priority;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.*;
import java.io.IOException;

/**
 * Created by chaojiewang on 5/3/18.
 */
@Priority(value = 50)
@Provider
@Secure
public class SecurityProvider implements ReaderInterceptor, ContainerRequestFilter, WriterInterceptor, ContainerResponseFilter {
    private static final Logger logger = LogManager.getLogger(SecurityProvider.class);
    private static final TokenManager tokenManager = SQLTokenManager.getManager();
    private static final String AUTH_TYPE = "Bearer";

    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        logger.info("reader interceptor.");
        return context.proceed();
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        logger.debug("filter 1");
        String bear = requestContext.getHeaders().getFirst("Authorization");

        int pos = bear.indexOf(' ');

        if (pos == -1)
            throw new UnauthorizationException();

        String type = bear.substring(0, pos).trim();
        String token = bear.substring(pos+1).trim();

        if (!type.equals(AUTH_TYPE))
            throw new UnauthorizationException();

        try {
            long userId = tokenManager.checkToken(token);

            // It is valid token. get user
            UsersManager usersManager = new UsersManager(DBManager.getManager().getDataSource(DBManager.DATABASE_DUDU_SHOPPING));
            User user = usersManager.getUser(userId);

            // set context
            DuduSecurityContext context = new DuduSecurityContext(user);
            requestContext.setSecurityContext(context);
        } catch (Exception e) {
            logger.warn("Invalid token:", e);
            throw new UnauthorizationException();
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        logger.info("response filter");
    }

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        logger.info("write interceptor");
        context.proceed();
    }
}
