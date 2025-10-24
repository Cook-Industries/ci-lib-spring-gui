/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.util;

import java.io.InputStream;
import java.util.List;

import org.springframework.core.io.DefaultResourceLoader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import de.cookindustries.lib.spring.gui.util.exception.ResourceLoaderException;

/**
 * Utility to load resources and transform them.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public final class ResourceLoader
{

    private static final org.springframework.core.io.ResourceLoader loader = new DefaultResourceLoader();
    private static final ObjectMapper                               mapper = new ObjectMapper();

    private ResourceLoader()
    {
        // prevent instantiation
    }

    /**
     * Load a resource file as a {@link InputStream}.
     * 
     * @param path to the resource file
     * @return the {@code InputStream}
     */
    public static InputStream load(String path)
    {
        try
        {
            return loader.getResource(path).getInputStream();
        }
        catch (Exception ex)
        {
            throw new ResourceLoaderException(path, ex);
        }
    }

    /**
     * Read a single object from a JSON file.
     * 
     * @param <T> the expected type of the object
     * @param path to the resource file
     * @param targetClass the expected type of the object
     * @return the read object
     */
    public static <T> T loadJsonFrom(String path, Class<T> targetClass)
    {
        try
        {
            InputStream inputStream = load(path);

            return mapper.readValue(inputStream, targetClass);
        }
        catch (Exception ex)
        {
            throw new ResourceLoaderException(path, targetClass, ex);
        }
    }

    /**
     * Read a list of objects from a JSON file.
     * 
     * @param <T> the expected type of list objects
     * @param path to the resource file
     * @param targetClass the expected type of list objects
     * @return a list for read objects
     */
    public static <T> List<T> loadJsonListFrom(String path, Class<T> targetClass)
    {
        try
        {
            InputStream    inputStream = load(path);

            CollectionType listType    = mapper
                .getTypeFactory()
                .constructCollectionType(List.class, targetClass);

            return mapper.readValue(inputStream, listType);
        }
        catch (Exception ex)
        {
            throw new ResourceLoaderException(path, targetClass, ex);
        }
    }

}
