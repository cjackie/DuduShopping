package com.dudu.shopping;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

/**
 * Created by chaojiewang on 4/26/18.
 */
public class RestJsonProvider extends JacksonJaxbJsonProvider {
    public RestJsonProvider() {
        super();

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        setMapper(mapper);
    }
}