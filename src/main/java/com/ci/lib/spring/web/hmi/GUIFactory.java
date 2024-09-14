/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
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

    /**
     * Read in a static template and transform into a {@link Container}
     * 
     * @param path to read in
     * 
     * @return the parsed {@code Container}
     */
    public Container fromComponentJson(String path)
    {
        JsonTreeMapper mapper = new JsonTreeMapper();

        JsonTreeRoot   root;
        try
        {
            root = mapper.map(path);

            return JsonMapper.map(root);
        }
        catch (Exception e)
        {
            throw new JsonMapperException(String.format("error building gui component [%s] : [%s]", path, e.getMessage()));
        }
    }

    /**
     * Read in a dynamic template and transform into a {@link Container}
     * 
     * @param path to read in
     * @param valueMap dynamic {@code valueMap}
     * 
     * @return the parsed {@code Container}
     */
    public Container fromComponentJson(String path, ValueMap valueMap)
    {
        JsonTreeMapper mapper = new JsonTreeMapper();

        JsonTreeRoot   root;
        try
        {
            root = mapper.map(path);

            return JsonMapper.map(root, valueMap);
        }
        catch (Exception e)
        {
            throw new JsonMapperException(String.format("error building gui component [%s] : [%s]", path, e.getMessage()));
        }
    }
}
