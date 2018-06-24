package com.dudu.rest.corner;

import com.dudu.rest.Secure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * Created by chaojiewang on 4/26/18.
 */
@Path("/ping")
public class Ping {

    @Context
    private SecurityContext securityContext;

    private static Logger logger = LogManager.getLogger(Ping.class);

    @GET
    public String ping() {
        logger.info("hello");
        return "ok";
    }

    @Secure
    @GET
    @Path("/secure")
    public String securePing() {
        logger.info("hello security");
        return "ok";
    }
}
