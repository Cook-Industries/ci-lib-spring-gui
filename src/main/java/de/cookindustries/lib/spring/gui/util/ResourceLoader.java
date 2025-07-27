package de.cookindustries.lib.spring.gui.util;

import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

/**
 * Utility to load resources and transform them.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public final class ResourceLoader
{

    private static final ObjectMapper mapper = new ObjectMapper();

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
        return ResourceLoader.class.getClassLoader().getResourceAsStream(path);
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
            throw new IllegalStateException(ex);
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
            throw new IllegalStateException(ex);
        }
    }

}
