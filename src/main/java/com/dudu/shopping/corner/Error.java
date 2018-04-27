package com.dudu.shopping.corner;

import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by chaojiewang on 4/26/18.
 */
@Path("/error")
public class Error {

    @GET
    @Path("/404")
    public String error404() {
        Response rsp = Response.status(Response.Status.NOT_FOUND)
                .entity("error")
                .type(MediaType.APPLICATION_JSON)
                .build();
        throw new NotFoundException(rsp);
    }

    @GET
    @Path("/500")
    public String error500() {
        Response rsp = Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("error")
                .type(MediaType.APPLICATION_JSON)
                .build();
        throw new InternalServerErrorException(rsp);
    }
}
