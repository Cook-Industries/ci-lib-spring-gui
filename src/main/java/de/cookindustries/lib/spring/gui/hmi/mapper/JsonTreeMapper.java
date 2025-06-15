/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * 
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.mapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.cookindustries.lib.spring.gui.hmi.mapper.exception.JsonMapperException;
import lombok.Data;

/**
 * Mapper for JSON files to {@link JsonTreeRoot}.
 */
@Data
public final class JsonTreeMapper
{

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Map a {@link JsonTreeRoot} from a {@code path}
     * 
     * @param path a jar resource path
     * @return the mapped resource
     * @throws JsonMapperException if anything goes wrong
     */
    public JsonTreeRoot map(String path)
    {
        try
        {
            InputStream inputStream = JsonTreeRoot.class.getClassLoader().getResourceAsStream(path);

            return mapper.readValue(inputStream, JsonTreeRoot.class);
        }
        catch (Exception e)
        {
            throw new JsonMapperException(e);
        }
    }

    /**
     * Map a {@link JsonTreeRoot} from a {@link File}
     * 
     * @param file a file resource
     * @return the mapped resource
     * @throws JsonMapperException if anything goes wrong
     */
    public JsonTreeRoot map(File file)
    {
        try
        {
            InputStream inputStream = new FileInputStream(file);

            return mapper.readValue(inputStream, JsonTreeRoot.class);
        }
        catch (Exception e)
        {
            throw new JsonMapperException(e);
        }
    }
}
