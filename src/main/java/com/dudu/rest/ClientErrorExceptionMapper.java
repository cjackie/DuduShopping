package com.dudu.rest;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ClientErrorExceptionMapper implements ExceptionMapper<ClientErrorException> {

    @Override
    public Response toResponse(ClientErrorException exception) {
        if (exception instanceof BadRequestException)
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
        else if (exception instanceof NotAuthorizedException)
            return Response.status(HttpServletResponse.SC_UNAUTHORIZED).build();
        else if (exception instanceof ForbiddenException)
            return Response.status(HttpServletResponse.SC_FORBIDDEN).build();
        else
            return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).build();
    }

}
