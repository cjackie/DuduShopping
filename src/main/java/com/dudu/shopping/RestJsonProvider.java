package com.dudu.shopping;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import javax.annotation.Priority;
import javax.ws.rs.ext.Provider;

/**
 * Created by chaojiewang on 4/26/18.
 */
@Priority(value = 100)
@Provider
public class RestJsonProvider extends JacksonJaxbJsonProvider {
    public RestJsonProvider() {
        super();

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        setMapper(mapper);
    }
}