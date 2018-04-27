package com.dudu.shopping.corner;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Created by chaojiewang on 4/26/18.
 */
@Path("/ping")
public class Ping {

    @GET
    @Path("/")
    public String ping() {
        return "ok";
    }
}
