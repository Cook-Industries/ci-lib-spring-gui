package com.ci.lib.spring.web.hmi.mapper;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Data
public final class JsonTreeMapper
{

    private final ObjectMapper mapper = new ObjectMapper();

    public JsonTreeRoot map(String path) throws IOException
    {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(path);

        return mapper.readValue(inputStream, JsonTreeRoot.class);
    }
}
