/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A {@code Service} to dissect {@link FlatMappable} objects to use them in a repetition instantiation of {@code Components}.
 * 
 * @since 3.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Service
public final class FlatMappableDissector
{

    private static final Logger                           LOG = LoggerFactory.getLogger(FlatMappableDissector.class);

    private final ObjectMapper                            objectMapper;

    private final Map<Class<?>, Function<Object, String>> transformers;

    /**
     * Create a {@code FlatMappableDissector}.
     * 
     * @param properties to use
     */
    public FlatMappableDissector(FlatMappableDissectorProperties properties)
    {
        this.objectMapper = new ObjectMapper();
        this.transformers = properties.getTransformers();
    }

    /**
     * Dissect a {@link FlatMappable} object and map it to a {@link Map}.
     * 
     * @param obj to dissect
     * @return the filled unmodifiable {@code Map}, or {@code null} if the input was {@code null}
     */
    public Map<String, Object> dissect(FlatMappable obj)
    {
        if (obj == null)
        {
            return null;
        }

        Map<String, Object> nestedMap = objectMapper.convertValue(obj, new TypeReference<Map<String, Object>>() {});
        Map<String, Object> flatMap   = new LinkedHashMap<>();

        flatten("", nestedMap, flatMap);

        return Collections.unmodifiableMap(flatMap);
    }

    /**
     * Flatten a given {@link Object}.
     * 
     * @param prefix to insert befor variable names in nested calls
     * @param value to flatten
     * @param result the result map to fill
     */
    private void flatten(String prefix, Object value, Map<String, Object> result)
    {
        if (value == null)
        {
            return;
        }

        if (value instanceof Map<?, ?> map)
        {
            for (Map.Entry<?, ?> entry : map.entrySet())
            {
                Object k = entry.getKey();

                if (!(k instanceof String))
                {
                    continue;
                }

                String key = prefix.isEmpty() ? (String) k : prefix + "." + k;

                flatten(key, entry.getValue(), result);
            }
        }
        else if (value instanceof List<?>)
        {
            LOG.warn("lists not supported");
        }
        else if (value != null && value.getClass().isArray())
        {
            LOG.warn("arrays not supported");
        }
        else
        {
            Class<?> valueClass = value.getClass();

            if (transformers.containsKey(valueClass))
            {
                result.put(prefix, transformers.get(valueClass).apply(value));
            }
            else
            {
                LOG.warn("dissection of unknown class: " + valueClass.getSimpleName());
            }
        }
    }
}
