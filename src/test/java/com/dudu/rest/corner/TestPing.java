package com.dudu.rest.corner;

import com.dudu.rest.RestJsonProvider;
import com.dudu.rest.RestTestBase;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;


/**
 * Created by chaojiewang on 4/27/18.
 */
public class TestPing extends RestTestBase {

    @Before
    public void setup() {
        Assume.assumeTrue(isReady());
    }

    Client client = ClientBuilder.newClient().register(RestJsonProvider.class);

    @Test
    public void test() {
        String rootUrl = System.getenv("ROOT_URL");
        if (rootUrl == null)
            rootUrl = "http://localhost:8081/dudu_shopping/rest/";

        Response response = client.target(rootUrl + "ping").request().get();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        String text = response.readEntity(String.class);
        Assert.assertEquals(text, "ok");
    }


    @Test
    public void test2() {
        Response response  = getClient().target(getUriBuilder().path("ping")).request().get();
        String text = response.readEntity(String.class);
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals(text, "ok");
    }

}
