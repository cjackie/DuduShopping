package com.dudu.rest;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ClientErrorExceptionMapper implements ExceptionMapper<ClientErrorException> {

    @Override
    public Response toResponse(ClientErrorException exception) {
        return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).build();
    }

}
