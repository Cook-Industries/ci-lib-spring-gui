/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.hmi.mapper;

import java.io.FileInputStream;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.cook_industries.lib.spring.gui.hmi.mapper.exception.JsonMapperException;
import lombok.Data;

@Data
public final class JsonTreeMapper
{

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Map a {@link JsonTreeRoot} from a {@code path} as a {@link InputStream} from a resource.
     * 
     * <p>
     * {@code Path} either can be a jar resource path starting with a '/' or an absolute file path.
     * 
     * @param path to the resource
     * 
     * @return the mapped resource
     * 
     * @throws JsonMapperException if anything goes wrong
     */
    public JsonTreeRoot map(String path)
    {
        InputStream inputStream;

        try
        {
            if (path.startsWith("/"))
            {
                inputStream = this.getClass().getClassLoader().getResourceAsStream(path);
            }
            else
            {
                inputStream = new FileInputStream(path);
            }

            return mapper.readValue(inputStream, JsonTreeRoot.class);
        }
        catch (Exception e)
        {
            throw new JsonMapperException(e);
        }
    }
}
