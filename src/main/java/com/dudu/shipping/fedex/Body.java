package com.dudu.shipping.fedex;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementUnion;

/**
 * Created by Chaojie (Jack) Wang on 6/18/18.
 */
public class Body {
    @ElementUnion({
            @Element(name=Request.VERSION + ":TrackRequest", type=TrackRequest.class),
    })
    private RequestBody requestBody;

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
    }
}
