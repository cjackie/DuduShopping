package com.dudu.shipping.fedex;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Chaojie (Jack) Wang on 6/18/18.
 * <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:v14="http://fedex.com/ws/track/v14">
 *     <soapenv:Header/>
 * <soapenv:Body>
 * <v14:TrackRequest>
 */
@Root(name = "soapenv:Envelope")
public class Request {
    public static final String VERSION = "v14";

    @Attribute(name = "xmlns:soapenv")
    private String xmlns = "http://schemas.xmlsoap.org/soap/envelope/";

    @Attribute(name = "xmlns:" + Request.VERSION)
    private String version = "http://fedex.com/ws/track/v14";

    @Element(name = "soapenv:Header")
    private String header = "";

    @Element(name = "soapenv:Body")
    private Body body;

    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public static Request buildRequest(RequestBody requestBody) {
        Body body = new Body();
        body.setRequestBody(requestBody);

        Request request = new Request();
        request.body = body;
        return request;
    }
}
