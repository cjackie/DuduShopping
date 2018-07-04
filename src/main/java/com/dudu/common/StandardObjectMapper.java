package com.dudu.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.SimpleDateFormat;

public class StandardObjectMapper extends ObjectMapper {
    public static StandardObjectMapper getInstance() {
        return instance;
    }

    private static StandardObjectMapper instance = new StandardObjectMapper();

    private StandardObjectMapper() {
        super();
        this.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
    }

}
