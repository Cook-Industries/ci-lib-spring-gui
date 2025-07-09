/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.cookindustries.lib.spring.gui.hmi.mapper.exception.JsonMapperException;
import lombok.Data;

/**
 * Mapper for JSON files to {@link JsonTreeRoot}.
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
public final class JsonTreeMapper
{

    private static final Logger LOG    = LoggerFactory.getLogger(JsonMapper.class);

    private final ObjectMapper  mapper = new ObjectMapper();

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
            LOG.trace("map content from resource [{}]", path);

            InputStream inputStream = JsonTreeRoot.class.getClassLoader().getResourceAsStream(path);

            return mapper.readValue(inputStream, JsonTreeRoot.class);
        }
        catch (Exception ex)
        {
            throw new JsonMapperException(ex);
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
            LOG.trace("map content from file [{}]", file);

            InputStream inputStream = new FileInputStream(file);

            return mapper.readValue(inputStream, JsonTreeRoot.class);
        }
        catch (Exception ex)
        {
            throw new JsonMapperException(ex);
        }
    }
}
