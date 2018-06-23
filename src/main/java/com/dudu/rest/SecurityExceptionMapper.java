package com.dudu.rest;

import com.dudu.rest.exceptions.SecurityException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SecurityExceptionMapper implements ExceptionMapper<SecurityException> {
    @Override
    public Response toResponse(SecurityException exception) {
        // TODO
        return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).build();
    }
}
