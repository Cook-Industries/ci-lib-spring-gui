package com.ci.lib.spring.web.hmi;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.ci.lib.spring.web.hmi.container.Container;
import com.ci.lib.spring.web.hmi.mapper.JsonMapper;
import com.ci.lib.spring.web.hmi.mapper.JsonTreeMapper;
import com.ci.lib.spring.web.hmi.mapper.JsonTreeRoot;
import com.ci.lib.spring.web.hmi.mapper.ValueMap;
import com.ci.lib.spring.web.hmi.mapper.exception.JsonMapperException;

import lombok.Data;

@Component
@Data
public class GUIFactory
{

    public Container fromComponentJson(String path)
    {
        JsonTreeMapper mapper = new JsonTreeMapper();

        JsonTreeRoot   root;
        try
        {
            root = mapper.map(path);

            return JsonMapper.map(root);
        }
        catch (IOException e)
        {
            throw new JsonMapperException(String.format("error while parsing [%s] : [%s]", path, e.getMessage()));
        }
    }

    public Container fromComponentJson(String path, ValueMap valueMap)
    {
        JsonTreeMapper mapper = new JsonTreeMapper();

        JsonTreeRoot   root;
        try
        {
            root = mapper.map(path);

            return JsonMapper.map(root, valueMap);
        }
        catch (IOException e)
        {
            throw new JsonMapperException(String.format("error while parsing [%s] : [%s]", path, e.getMessage()));
        }
    }
}
