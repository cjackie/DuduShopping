package com.dudu.rest;

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
    private static Logger logger = LogManager.getLogger(SecurityProvider.class);

    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        logger.info("reader interceptor.");
        return context.proceed();
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        logger.info("request filter");

        // TODO



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
