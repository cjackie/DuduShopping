package com.dudu.rest;

import com.dudu.database.DBManager;
import com.dudu.users.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Priority;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
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
            throw new BadRequestException();

        String type = bear.substring(0, pos).trim();
        String token = bear.substring(pos+1).trim();

        if (!type.equals(AUTH_TYPE))
            throw new BadRequestException();

        User user;
        try {
            long userId = tokenManager.checkToken(token);

            // It is valid token. get user
            UsersManager usersManager = new UsersManager(DBManager.getManager().getDataSource(DBManager.DATABASE_DUDU_SHOPPING));
            user = usersManager.getUser(userId);
        } catch (Exception e) {
            logger.warn("Invalid token:", e);
            throw new NotAuthorizedException("Invalid token");
        }

        // check on scope
        String apiEndpoint = requestContext.getUriInfo().getAbsolutePath().getPath();
        String method = requestContext.getMethod();
        if (!ApiEndpointChecker.getInstance().check(apiEndpoint, method, user.getScopes()))
            throw new ForbiddenException();

        // set context
        DuduSecurityContext context = new DuduSecurityContext(user);
        requestContext.setSecurityContext(context);
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
